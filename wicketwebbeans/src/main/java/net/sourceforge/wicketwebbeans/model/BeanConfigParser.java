/*---
   Copyright 2006-2007 Visual Systems Corporation.
   http://www.vscorp.com

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
   
        http://www.apache.org/licenses/LICENSE-2.0
   
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
---*/

package net.sourceforge.wicketwebbeans.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * A recursive descent parser to parse a WWB bean configuration stream.
 * <p>
 * 
 * @author Dan Syrstad
 */
public class BeanConfigParser {
    private static final char QUOTE_CHAR = '"';

    private String streamName;
    private InputStream stream;
    private StreamTokenizer tokenizer;

    /**
     * Construct a BeanPropsParser.
     * 
     * @param stream
     * @param beanMetaData
     * @param context
     */
    public BeanConfigParser(String streamName, InputStream stream) {
        this.streamName = streamName;
        this.stream = stream;
    }

    /**
     * Generate a error message via an exception.
     */
    private RuntimeException generateError(String msg) {
        return new RuntimeException("Error: " + streamName + " at line "
                + tokenizer.lineno() + ": " + msg);
    }

    /**
     * @return the current token from the tokenizer. Note that this returns an
     *         empty string rather than null.
     */
    private String getToken() {
        String value = tokenizer.sval;
        if (value == null && tokenizer.ttype >= 0) {
            value = String.valueOf((char) tokenizer.ttype);
        }

        return value == null ? "" : value;
    }

    /**
     * Gets the next token from the tokenizer. Note that this returns an empty
     * string rather than null. This is a combination of tokenizer.nextToken()
     * and getToken().
     * 
     * @return the next token.
     */
    private String getNextToken() {
        try {
            if (tokenizer.nextToken() == StreamTokenizer.TT_EOF) {
                throw new RuntimeException("Unexpected EOF reading from "
                        + streamName);
            }

            return getToken();
        } catch (IOException e) {
            throw new RuntimeException("Error reading from " + streamName, e);
        }

    }

    /**
     * Generate an "expected" error message via an exception.
     */
    private RuntimeException generateExpectedError(String expectedThing) {
        return generateError("Expected '" + expectedThing + "', but got '"
                + getToken() + "'");
    }

    /**
     * Reads and expects a token.
     * 
     * @param token
     */
    private void expect(String token) {
        if (!getNextToken().equals(token)) {
            throw generateExpectedError(token);
        }
    }

    /**
     * Parses the stream given on construction and returns the BeanASTs.
     * 
     * @return a list of BeanASTs.
     * 
     * @throws RuntimeException
     *             if a parsing error occurs.
     */
    public List<BeanConfigAST> parse() {
        tokenizer = new StreamTokenizer(new BufferedReader(
                new InputStreamReader(stream)));
        tokenizer.resetSyntax();
        tokenizer.wordChars('a', 'z');
        tokenizer.wordChars('A', 'Z');
        tokenizer.wordChars('0', '9');
        tokenizer.wordChars('_', '_');
        tokenizer.wordChars('-', '-');
        tokenizer.wordChars('.', '.');
        tokenizer.wordChars('*', '*');
        tokenizer.wordChars('$', '$'); // For inner classes/macros
        tokenizer.wordChars(128 + 32, 255);
        tokenizer.whitespaceChars(0, ' ');
        tokenizer.commentChar('#');
        tokenizer.quoteChar(QUOTE_CHAR);

        List<BeanConfigAST> beans = new ArrayList<BeanConfigAST>();
        try {
            while (tokenizer.nextToken() != StreamTokenizer.TT_EOF) {
                if (tokenizer.ttype == StreamTokenizer.TT_WORD) {
                    beans.add(parseBean());
                } else {
                    throw generateExpectedError("bean name");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading " + streamName, e);
        }

        return beans;
    }

    private BeanConfigAST parseBean() {
        String beanName = getToken();

        expect("{");

        // Allow empty blocks...
        List<ParameterAST> params;
        if (!getNextToken().equals("}")) {
            tokenizer.pushBack();
            params = parseParameters();
            expect("}");
        } else {
            params = new ArrayList<ParameterAST>();
        }

        return new BeanConfigAST(beanName, params);
    }

    private List<ParameterAST> parseParameters() {
        List<ParameterAST> params = new ArrayList<ParameterAST>();
        while (true) {
            params.add(parseParameter());

            String nextToken = getNextToken();
            tokenizer.pushBack();

            if (nextToken.equals("}")) {
                break;
            }
        }

        return params;
    }

    private ParameterAST parseParameter() {
        String paramName = getNextToken();
        expect(":");

        List<ParameterValueAST> values = parseParameterValues();

        String nextToken = getNextToken();
        if (!nextToken.equals(";")) {
            if (nextToken.equals("}")) {
                tokenizer.pushBack();
            } else {
                throw generateExpectedError(";' or '}");
            }
        }

        return new ParameterAST(paramName, values);
    }

    private List<ParameterValueAST> parseParameterValues() {
        List<ParameterValueAST> paramValues = new ArrayList<ParameterValueAST>();
        do {
            paramValues.add(parseParameterValue());
        } while (getNextToken().equals(","));

        tokenizer.pushBack();
        return paramValues;
    }

    private ParameterValueAST parseParameterValue() {
        String value = getNextToken();
        boolean isLiteral = tokenizer.ttype == QUOTE_CHAR
                || (tokenizer.ttype == StreamTokenizer.TT_WORD && StringUtils
                        .containsOnly(value, "0123456789-."))
                || (tokenizer.ttype == StreamTokenizer.TT_WORD && (value
                        .equals("true") || value.equals("false")));

        if (!isLiteral) {
            if (value.equals(";")) {
                throw generateError("Value expected, not ';'");
            } else if (value.equals("null")) {
                value = null;
            }
        }

        ParameterValueAST param = new ParameterValueAST(value, isLiteral);
        String nextToken = getNextToken();
        // Start of parameters for value?
        if (nextToken.equals("{")) {
            // Allow empty blocks...
            if (!getNextToken().equals("}")) {
                tokenizer.pushBack();
                param.setSubParameters(parseParameters());
                expect("}");
            }
        } else {
            tokenizer.pushBack();
        }

        return param;
    }
}
