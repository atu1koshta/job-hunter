package com.unemployed.message.email;
import com.unemployed.model.EmailContent;

import java.io.IOException;

public abstract class EmailTemplate {
    public String greeting(String recipient) {
        return "<p>Hi " + recipient + ",</p>";
    };
    public String signature() {
        return "<p style=\"font-family: 'Courier New', monospace;\">"
                + "Best Regards,"
                + "<br>"
                + "Atul Koshta"
                + "<br>"
                + "Phone: +91 9713443774"
                + "<br>"
                + "Email: atulk2018@gmail.com"
                + "<br> "
                + "LinkedIn: <a href='https://www.linkedin.com/in/atulkoshta/'>https://www.linkedin.com/in/atulkoshta/</a>"
                + "<br>"
                + "GitHub: <a href='https://github.com/atu1koshta'>https://github.com/atu1koshta</a>"
                + "</p>";
    };

    public String closing() {
        throw new UnsupportedOperationException("Closing method must be overridden");
    }

    public String closing_with_role_company(String role, String company) {
        throw new UnsupportedOperationException("Closing method must be overridden");
    }

    public String enclosure() {
        return "<p><strong>Enclosure: Resume</strong></p>";
    };

    public abstract String readMainContentTemplate(String company, String role) throws IOException;
}
