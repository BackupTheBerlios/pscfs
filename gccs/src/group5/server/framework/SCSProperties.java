//$Id: SCSProperties.java,v 1.6 2005/06/13 11:18:22 huuhoa Exp $
package group5.server.framework;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.csapi.fw.TpServiceProperty;

public final class SCSProperties {

	private Map m_mapProperties;

	public SCSProperties(String serviceName, String serviceVersion) {
		m_mapProperties = new HashMap();
		m_mapProperties.put("P_SERVICE_NAME", new String[] { serviceName });
		m_mapProperties.put("P_SERVICE_VERSION", new String[] { serviceVersion });
		m_mapProperties.put("P_PRODUCT_NAME", new String[] { "Group 5 - Lab" });
		m_mapProperties.put("P_PRODUCT_VERSION", new String[] { "1.0" });
	}

	public void setProperty(String sKey, String propertyList[]) {
		m_mapProperties.put(sKey, propertyList);
	}

	TpServiceProperty[] getServicePropertyList() {
		TpServiceProperty aTpServiceProperty[] = new TpServiceProperty[m_mapProperties
				.size()];
		int i = 0;
		for (Iterator iterator = m_mapProperties.entrySet().iterator(); iterator
				.hasNext();) {
			java.util.Map.Entry entry = (java.util.Map.Entry) iterator.next();
			aTpServiceProperty[i] = new TpServiceProperty((String) entry
					.getKey(), (String[]) entry.getValue());
			i++;
		}

		return aTpServiceProperty;
	}

	String getServiceName() {
		String srvName[] = (String[]) m_mapProperties.get("P_SERVICE_NAME");
		return srvName[0];
	}

	String getServiceVersion() {
		String srvVersion[] = (String[]) m_mapProperties.get("P_SERVICE_VERSION");
		return srvVersion[0];
	}

}
