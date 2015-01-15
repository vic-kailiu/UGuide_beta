package com.kai.uGuide;

import java.io.Serializable;

public class AtomPayment implements Serializable {
	private static final long serialVersionUID = -5435670920302756945L;
	
	private String name = "";
	private double value = 0;
    private String speech;

	public AtomPayment(String name, double value, String speech) {
		this.setName(name);
		this.setValue(value);
        this.setSpeech(speech);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

    public String getSpeech() {
        return speech;
    }

    public void setSpeech(String speech) {
        this.speech = speech;
    }
}