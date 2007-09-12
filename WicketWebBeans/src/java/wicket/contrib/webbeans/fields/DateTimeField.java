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

package wicket.contrib.webbeans.fields;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import wicket.ResourceReference;
import wicket.contrib.webbeans.model.ElementMetaData;
import wicket.extensions.markup.html.datepicker.DatePicker;
import wicket.extensions.markup.html.datepicker.DatePickerSettings;
import wicket.extensions.markup.html.form.DateTextField;
import wicket.markup.html.basic.Label;
import wicket.markup.html.form.FormComponent;
import wicket.markup.html.panel.Fragment;
import wicket.model.IModel;
import wicket.util.convert.ConversionException;
import wicket.util.convert.IConverter;
import wicket.util.convert.converters.DateConverter;

/**
 * Date/Time Field component. Implemented as a text field combined with a DatePicker. <p>
 * 
 * @author Dan Syrstad
 */
public class DateTimeField extends AbstractField
{
    // TODO Make Locale specific - or configurable. This is ISO Format
    // TODO Look for "DateTimeField.dateFmt" in Localizer.
    public static final String DATE_FMT_STR = "yyyy-MM-dd";
    public static final String TIME_FMT_STR = "HH:mm";
    public static final String DATE_TIME_FMT_STR = DATE_FMT_STR + ' ' + TIME_FMT_STR;
    public static final String DATE_TIME_ZONE_FMT_STR = DATE_TIME_FMT_STR + " z";
    
    private String fmt;

    /**
     * Construct a new DateTimeField.
     *
     * @param id the Wicket id for the editor.
     * @param model the model.
     * @param metaData the meta data for the property.
     * @param viewOnly true if the component should be view-only.
     */
    public DateTimeField(String id, IModel model, ElementMetaData metaData, boolean viewOnly)
    {
        super(id, model, metaData, viewOnly);
        
        Class type = metaData.getPropertyType();
        boolean displayTz = false;
        if (Time.class.isAssignableFrom(type)) {
            fmt = TIME_FMT_STR;
        }
        else if (java.sql.Date.class.isAssignableFrom(type)) {
            fmt = DATE_FMT_STR;
        }
        else if (Calendar.class.isAssignableFrom(type)) {
            fmt = viewOnly ? DATE_TIME_ZONE_FMT_STR : DATE_TIME_FMT_STR;
            displayTz = true;
        }
        else { // if (Date.class.isAssignableFrom(type) || Timestamp.class.isAssignableFrom(type)) 
            fmt = DATE_TIME_FMT_STR;
        }
        
        final InternalDateConverter converter = new InternalDateConverter(); 
        Fragment fragment;
        if (viewOnly) {
            fragment = new Fragment("frag", "viewer");
            Label label = new LabelWithMinSize("date", model) {
                @Override
                public IConverter getConverter()
                {
                    return converter;
                }
            };
            
            fragment.add(label);
        }
        else {
            fragment = new Fragment("frag", "editor");

            FormComponent dateField = new DateTextField("dateTextField", model) {
                @Override
                public IConverter getConverter()
                {
                    return converter;
                }
            };
            
            fragment.add(dateField);
    
            DatePickerSettings settings = new DatePickerSettings();
            settings.setStyle( settings.newStyleWinter() );
            settings.setIcon( new ResourceReference(this.getClass(), "calendar.gif") );
    
            DatePicker picker = new DatePicker("datePicker", dateField, settings);
            // This sucks! It expects a DateConverter. I've got my own.
            DateConverter dateConverter = new DateConverter();
            SimpleDateFormat dateFmt = new SimpleDateFormat(fmt);
            dateFmt.setTimeZone(TimeZone.getTimeZone("GMT"));
            dateConverter.setDateFormat(Locale.getDefault(), dateFmt);
            picker.setDateConverter(dateConverter); 
            fragment.add(picker);

            if (displayTz) {
                Label label = new Label("timezone", model) {
                    @Override
                    public IConverter getConverter()
                    {
                        return new TimeZoneConverter();
                    }
                };
                
                fragment.add(label);
            }
            else {
                fragment.add( new Label("timezone", "") );
            }
        }

        add(fragment);
    }

    private final class InternalDateConverter implements IConverter 
    {
        private Locale locale = null;

        public Object convert(Object value, Class c)
        {
            if (value == null || value.getClass() == c) {
                return value;
            }
            
            // Convert Calendar or Date to String.
            if (String.class == c) {
                SimpleDateFormat dateFmt = new SimpleDateFormat(fmt);
                if (value instanceof Calendar) {
                    Calendar cal = (Calendar)value;
                    dateFmt.setTimeZone( cal.getTimeZone() );
                    return dateFmt.format( cal.getTime() );
                }
                
                if (value instanceof Date) {
                    dateFmt.setTimeZone(TimeZone.getTimeZone("GMT"));
                    return dateFmt.format((Date)value);
                }
                
                return null;
            }
            
            // Convert String to Calendar or sql.Date/Time/Timestamp
            Date date = null;
            if (value instanceof String) {
                // First convert it to a date. We think it's GMT because no TZ is specified in the String.
                SimpleDateFormat dateFmt = new SimpleDateFormat(fmt);
                dateFmt.setTimeZone(TimeZone.getTimeZone("GMT"));
                try {
                    date = dateFmt.parse((String)value);
                }
                catch (ParseException e) {
                    throw new ConversionException("Cannot convert '" + value + "' to a Date.").setSourceValue(value)
                        .setTargetType(c).setConverter(this).setLocale(locale);
                }
            }
            else if (value instanceof Date) {
                date = (Date)value;
            }
            
            if (date != null) {
                if (Timestamp.class == c) {
                    return new Timestamp( date.getTime() );
                }
                
                if (java.sql.Date.class == c) {
                    return new java.sql.Date( date.getTime() );
                }
                
                if (Time.class == c) {
                    return new Time( date.getTime() );
                }
                
                if (Date.class == c) {
                    return date;
                }
                
                if (Calendar.class.isAssignableFrom(c)) {
                    Calendar cal = new GregorianCalendar( TimeZone.getTimeZone("GMT") );
                    cal.setTime(date);
                    return cal;
                }
            }
            
            throw new RuntimeException("Don't know how to convert " + value.getClass() + " to a " + c);
        }

        public Locale getLocale()
        {
            return locale;
        }

        public void setLocale(Locale locale)
        {
            this.locale = locale;
        }
    }

    private final class TimeZoneConverter implements IConverter 
    {
        private Locale locale = Locale.getDefault();

        public Object convert(Object value, Class c)
        {
            if (value == null || value.getClass() == c) {
                return value;
            }
            
            // Convert Calendar's Timezone to a String.
            if (String.class == c) {
                SimpleDateFormat dateFmt = new SimpleDateFormat(fmt);
                if (value instanceof Calendar) {
                    Calendar cal = (Calendar)value;
                    TimeZone zone = cal.getTimeZone(); 
                    return zone.getDisplayName( zone.inDaylightTime(cal.getTime()), TimeZone.SHORT, locale);
                }
            }
            
            throw new RuntimeException("Don't know how to convert " + value.getClass() + " to a " + c);
        }

        public Locale getLocale()
        {
            return locale;
        }

        public void setLocale(Locale locale)
        {
            this.locale = locale;
        }
    }
}
