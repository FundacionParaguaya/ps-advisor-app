package org.fundacionparaguaya.adviserplatform.data.model;

/**
 * A country.
 */

public class Country {
    private long id;
    private String name;
    private String country;
    private String numericCode;
    private String alfa2Code;
    private String alfa3code;

    public Country(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getNumericCode() {
        return numericCode;
    }

    public void setNumericCode(String numericCode) {
        this.numericCode = numericCode;
    }

    public String getAlfa2Code() {
        return alfa2Code;
    }

    public void setAlfa2Code(String alfa2Code) {
        this.alfa2Code = alfa2Code;
    }

    public String getAlfa3code() {
        return alfa3code;
    }

    public void setAlfa3code(String alfa3code) {
        this.alfa3code = alfa3code;
    }
}
