package group5.server;

import java.util.*;
import org.csapi.fw.TpServiceProperty;

public final class SCSProperties {

	private Map m_mapProperties;

	public SCSProperties(String s, String s1) {
		m_mapProperties = new HashMap();
		m_mapProperties.put("P_SERVICE_NAME", new String[] { s });
		m_mapProperties.put("P_SERVICE_VERSION", new String[] { s1 });
		m_mapProperties.put("P_PRODUCT_NAME", new String[] { "Group 5 - Lab" });
		m_mapProperties.put("P_PRODUCT_VERSION", new String[] { "1.0" });
	}

	public void setProperty(String s, String as[]) {
		m_mapProperties.put(s, as);
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
		String as[] = (String[]) m_mapProperties.get("P_SERVICE_NAME");
		return as[0];
	}

	String getServiceVersion() {
		String as[] = (String[]) m_mapProperties.get("P_SERVICE_VERSION");
		return as[0];
	}

}
