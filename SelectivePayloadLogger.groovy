/**
* Refer the link below to learn more about the use cases of script.
* https://help.sap.com/viewer/368c481cd6954bdfa5d0435479fd4eaf/Cloud/en-US/148851bf8192412cba1f9d2c17f4bd25.html
*
* If you want to know more about the SCRIPT APIs, refer the link below
* https://help.sap.com/doc/a56f52e1a58e4e2bac7f7adbf45b2e26/Cloud/en-US/index.html 
*/
import com.sap.gateway.ip.core.customdev.util.Message;
import java.util.HashMap;

/**
 * This script enables selective logging of message payloads during an integration process. 
 * It offers configuration options to log the input payload, payload after mapping, and 
 * payload in case of errors.
 *
 * @author Aman Anand/CPI
 * @version 1.0 
 */

def Message processData(Message message) {
    // Retrieve the message body as a String
    String body = message.getBody(String);

    // Get integration-specific properties
    def properties = message.getProperties()

    /**
     * Configuration for payload logging:
     *  inputEnabled:  Controls logging of the initial input payload (YES/NO)
     *  inputMessage:  The log message to use for the input payload
     *  errorEnabled:  Controls logging of the payload in case of errors (YES/NO)
     *  errorMessage:  The log message to use when logging errors 
     *  mappingEnabled: Controls logging of the payload after mapping (YES/NO)
     *  mappingMessage: The log message to use for the post-mapping payload
     */
    HashMap<String, String> payloadLoggingConfig = [
        inputEnabled:  properties.get("InputPayloadLog"), 
        inputMessage:  "InputPayload",
        errorEnabled:  properties.get("InputPayloadLogOnError"),
        errorMessage:  "onErrorInputPayload",
        mappingEnabled: properties.get("AfterMappingPayload"),
        mappingMessage:  "afterMappingInputPayload"
    ]

    // Determine if logging should be performed
    if (isLoggingEnabled(payloadLoggingConfig)) {
        def messageLog = messageLogFactory.getMessageLog(message);
        if (messageLog != null) {
            String logMessage = getLogMessage(payloadLoggingConfig);
            messageLog.addAttachmentAsString(logMessage, body, "text/xml"); 
        }
    }

    return message;
}

/**
 * Helper function to determine if any type of payload logging is enabled.
 * @param config The payload logging configuration map
 * @return true if input, error, or mapping logging is enabled, false otherwise
 */
private boolean isLoggingEnabled(HashMap<String, String> config) {
    return config.inputEnabled.toUpperCase() == "YES" ||
           config.errorEnabled.toUpperCase() == "YES" ||
           config.mappingEnabled.toUpperCase() == "YES";
}

/**
 * Helper function to retrieve the appropriate log message based on the configuration.
 * @param config The payload logging configuration map
 * @return The log message string
 */
private String getLogMessage(HashMap<String, String> config) {
    if (config.inputEnabled.toUpperCase() == "YES") {
        return config.inputMessage;
    } else if (config.errorEnabled.toUpperCase() == "YES") {
        return config.errorMessage;
    } else { // Assume config.mappingEnabled == "YES"
        return config.mappingMessage;
    }
}
