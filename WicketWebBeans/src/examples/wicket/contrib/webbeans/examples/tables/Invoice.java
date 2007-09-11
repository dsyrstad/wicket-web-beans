package wicket.contrib.webbeans.examples.tables;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import wicket.contrib.webbeans.examples.nested.Address;

public class Invoice implements Serializable 
{
    private static final long serialVersionUID = -8500883418534059147L;

    private String invoiceNumber;
    private String customerName;
    private Address shipToAddress;
    private List<InvoiceLine> lines = new ArrayList<InvoiceLine>();

    public Invoice()
    {
    }

    public String getInvoiceNumber()
    {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber)
    {
        this.invoiceNumber = invoiceNumber;
    }

    public String getCustomerName()
    {
        return customerName;
    }

    public void setCustomerName(String customerName)
    {
        this.customerName = customerName;
    }

    public Address getShipToAddress()
    {
        return shipToAddress;
    }

    public void setShipToAddress(Address shipToAddress)
    {
        this.shipToAddress = shipToAddress;
    }

    public List<InvoiceLine> getLines()
    {
        return lines;
    }

    public void setLines(List<InvoiceLine> lines)
    {
        this.lines = lines;
    }
}