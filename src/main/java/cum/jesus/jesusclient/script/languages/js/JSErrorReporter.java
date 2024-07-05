package cum.jesus.jesusclient.script.languages.js;

import cum.jesus.jesusclient.util.Logger;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;

public final class JSErrorReporter implements ErrorReporter {
    @Override
    public void warning(String message, String sourceName, int line, String lineSource, int lineOffset) {
        reportErrorMessage(message, sourceName, line, lineSource, lineOffset, true);
    }

    @Override
    public void error(String message, String sourceName, int line, String lineSource, int lineOffset) {
        reportErrorMessage(message, sourceName, line, lineSource, lineOffset, false);
    }

    @Override
    public EvaluatorException runtimeError(String message, String sourceName, int line, String lineSource, int lineOffset) {
        return new EvaluatorException(message, sourceName, line, lineSource, lineOffset);
    }

    private void reportErrorMessage(String inputMessage, String sourceName, int line, String lineSource, int lineOffset, boolean isWarning) {
        String message;
        if (line > 0) {
            if (sourceName == null) {
                message = "line " + line + ": " + inputMessage;
            } else {
                message = "\"" + sourceName + "\" line " + line + ": " + inputMessage;
            }
        } else {
            message = inputMessage;
        }

        if (isWarning) {
            message = "Warning: " + message;
            Logger.warn(message);
        } else {
            Logger.error(message);
        }
    }
}
