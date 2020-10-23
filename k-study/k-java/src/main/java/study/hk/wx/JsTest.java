package study.hk.wx;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * @author HK
 * @date 2020-05-06 11:16
 */
public class JsTest {

    public static void main(String[] args) throws ScriptException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine script = manager.getEngineByName("javascript");
        script.eval("var a = 3;var b = 4;print(a+b)");
    }
}
