/*
 */
package dynamicservice.io.xml;

/**
 *
 * @author migo
 */
public abstract class AbstractNode {
    private String _nodeXml;

    /**
     *
     * @return
     */
    public String getNodeXML() {
        return _nodeXml;
    }

    /**
     *
     * @param _xml
     */
    public void setNodeXML(String _xml) {
        this._nodeXml = _xml;
    }
    
}
