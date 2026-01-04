package org.example.gui;

import javax.swing.text.*;

//https://www.tutorialspoint.com/how-can-we-make-jtextfield-accept-only-numbers-in-java#:~:text=Using%20DocumentFilter,-A%20DocumentFilter%20lets&text=The%20DocumentFilter()%20method%20creates,will%20intercept%20all%20text%20modifications.&text=Called%20when%20text%20is%20inserted,%22%5Cd%2B%22%20regular%20expression.
public class IntegerFilter extends DocumentFilter {

    @Override
    public void insertString(FilterBypass fb, int offset,
                             String string, AttributeSet attr)
            throws BadLocationException {

        // allow only digits
        if (string.matches("\\d+")) {
            super.insertString(fb, offset, string, attr);
        }
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length,
                        String text, AttributeSet attrs)
            throws BadLocationException {

        // allow digits or empty string (for backspace)
        if (text.matches("\\d*")) {
            super.replace(fb, offset, length, text, attrs);
        }
    }
}
