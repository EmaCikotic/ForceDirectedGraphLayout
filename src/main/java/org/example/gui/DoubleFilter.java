package org.example.gui;

import javax.swing.text.*;

public class DoubleFilter extends DocumentFilter {

    @Override
    public void insertString(FilterBypass fb, int offset,
                             String string, AttributeSet attr)
            throws BadLocationException {

        Document doc = fb.getDocument();
        String current = doc.getText(0, doc.getLength());

        String result = current.substring(0, offset)
                + string
                + current.substring(offset);

        if (result.matches("\\d*(\\.\\d*)?")) {
            super.insertString(fb, offset, string, attr);
        }
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length,
                        String text, AttributeSet attrs)
            throws BadLocationException {

        Document doc = fb.getDocument();
        String current = doc.getText(0, doc.getLength());

        String result = current.substring(0, offset)
                + text
                + current.substring(offset + length);

        if (result.matches("\\d*(\\.\\d*)?")) {
            super.replace(fb, offset, length, text, attrs);
        }
    }
}