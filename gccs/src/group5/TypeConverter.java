package group5;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.csapi.cc.gccs.TpCallReport;
import org.csapi.cc.gccs.TpCallReportType;

public class TypeConverter
{
	/**
	 * m_logger for the system
	 */
	static private Logger m_logger;
	
	static {
		m_logger = Logger.getLogger(TypeConverter.class);
	}

    public TypeConverter()
    {
    }

    public static int convertCallReportEvent(TpCallReport t)
    {
        if (m_logger.isEnabledFor(Level.INFO))
            m_logger.info("Entering convertCallReportEvent!");
        switch(t.CallReportType.value())
        {
        case 2: // '\002'
            return 256;

        case 3: // '\003'
            return 128;

        case 4: // '\004'
            return 8;

        case 6: // '\006'
            return 1024;

        case 5: // '\005'
            return 32;

        case 11: // '\013'
            return 16;

        case 7: // '\007'
            return 512;

        case 9: // '\t'
            return 64;

        case 0: // '\0'
        case 1: // '\001'
        case 8: // '\b'
        case 10: // '\n'
        default:
            return 0;
        }
    }

    public static TpCallReportType convertEvent(int event)
    {
        if (m_logger.isEnabledFor(Level.INFO))
            m_logger.info("Entering convertEvent:" + event);
        switch(event)
        {
        case 256: 
            return TpCallReportType.P_CALL_REPORT_ALERTING;

        case 128: 
            return TpCallReportType.P_CALL_REPORT_ANSWER;

        case 8: // '\b'
            return TpCallReportType.P_CALL_REPORT_BUSY;

        case 32: // ' '
            return TpCallReportType.P_CALL_REPORT_NO_ANSWER;

        case 1024: 
            return TpCallReportType.P_CALL_REPORT_DISCONNECT;

        case 512: 
            return TpCallReportType.P_CALL_REPORT_REDIRECTED;

        case 64: // '@'
            return TpCallReportType.P_CALL_REPORT_ROUTING_FAILURE;

        case 16: // '\020'
            return TpCallReportType.P_CALL_REPORT_NOT_REACHABLE;
        }
        return TpCallReportType.P_CALL_REPORT_UNDEFINED;
    }

    static public String eventName(int event)
    {
        if (m_logger.isEnabledFor(Level.INFO))
            m_logger.info("Entering eventName:" + event);
        switch(event)
        {
        case 0: // '\0'
            return "P_EVENT_NAME_UNDEFINED";

        case 1: // '\001'
            return "P_EVENT_GCCS_OFFHOOK_EVENT";

        case 2: // '\002'
            return "P_EVENT_GCCS_ADDRESS_COLLECTED_EVENT";

        case 4: // '\004'
            return "P_EVENT_GCCS_ADDRESS_ANALYSED_EVENT";

        case 8: // '\b'
            return "P_EVENT_GCCS_CALLED_PARTY_BUSY";

        case 16: // '\020'
            return "P_EVENT_GCCS_CALLED_PARTY_UNREACHABLE";

        case 32: // ' '
            return "P_EVENT_GCCS_NO_ANSWER_FROM_CALLED_PARTY";

        case 64: // '@'
            return "P_EVENT_GCCS_ROUTE_SELECT_FAILURE";

        case 128: 
            return "P_EVENT_GCCS_ANSWER_FROM_CALL_PARTY";
        }
        return "<unknown event>";
    }
}
