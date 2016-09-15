/**
 * ParserException.java
 * @author Vagisha Sharma
 * Jul 14, 2008
 * @version 1.0
 */
package org.yeastrc.proxl.import_xml_to_db.spectrum.common.exceptions;

/**
 * 
 */
public class DataProviderException extends Exception {

    private static final long serialVersionUID = 1L;
    
    private int lineNum = -1;
    private String line;
    private String message;
    
    public DataProviderException(String message) {
        super(message);
        this.message = message;
    }
    
    public DataProviderException(String message, Exception e) {
        super(message, e);
        this.message = message;
    }
    
    public DataProviderException(int lineNum, String message, String line) {
        super(message);
        this.message = message;
        this.lineNum = lineNum;
        this.line = line;
    }
    
    public DataProviderException(int lineNum, String message, String line, Exception e) {
        super(message, e);
        this.message = message;
        this.lineNum = lineNum;
        this.line = line;
    }
    
    public int getLineNum() {
        return this.lineNum;
    }
    
    public String getLine() {
        return line;
    }
    
    public String getErrorMessage() {
        return message;
    }
    
    public String getMessage() {
        StringBuilder buf = new StringBuilder();
        buf.append(message);
        if (lineNum != -1)
            buf.append("\n\tLINE NUMBER: "+lineNum);
        if (line != null)
            buf.append("\n\tLINE: "+line);
        return buf.toString();
    }
}
