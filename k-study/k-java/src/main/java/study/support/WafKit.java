/**
 * Copyright (c) 2011-2014, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package study.support;

import java.util.regex.Pattern;

/**
 * Web防火墙工具类
 * <p>
 *
 * @author hubin
 * @date 2014-5-8
 */
public class WafKit {

    private static Pattern pattern1 = Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE);

    private static Pattern pattern2 = Pattern.compile("</script>", Pattern.CASE_INSENSITIVE);

    private static Pattern pattern3 = Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE
            | Pattern.MULTILINE | Pattern.DOTALL);

    private static Pattern pattern4 = Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE
            | Pattern.MULTILINE | Pattern.DOTALL);

    private static Pattern pattern5 = Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE
            | Pattern.MULTILINE | Pattern.DOTALL);

    private static Pattern pattern6 = Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE);

    private static Pattern pattern7 = Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE);

    private static Pattern pattern8 = Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE
            | Pattern.MULTILINE | Pattern.DOTALL);

    /**
     * 过滤XSS脚本内容
     */
    public static String stripXSS(String value) {
        String rlt = null;

        if (null != value) {
            // NOTE: It's highly recommended to use the ESAPI library and uncomment the following line to

            rlt = value.replaceAll("", "");

            // Avoid anything between script tags
            Pattern scriptPattern = pattern1;
            rlt = scriptPattern.matcher(rlt).replaceAll("");

            // Avoid anything in a src='...' type of expression


            // Remove any lonesome </script> tag
            scriptPattern = pattern2;
            rlt = scriptPattern.matcher(rlt).replaceAll("");

            // Remove any lonesome <script ...> tag
            scriptPattern = pattern3;
            rlt = scriptPattern.matcher(rlt).replaceAll("");

            // Avoid eval(...) expressions
            scriptPattern = pattern4;
            rlt = scriptPattern.matcher(rlt).replaceAll("");

            // Avoid expression(...) expressions
            scriptPattern = pattern5;
            rlt = scriptPattern.matcher(rlt).replaceAll("");

            // Avoid javascript:... expressions
            scriptPattern = pattern6;
            rlt = scriptPattern.matcher(rlt).replaceAll("");

            // Avoid vbscript:... expressions
            scriptPattern = pattern7;
            rlt = scriptPattern.matcher(rlt).replaceAll("");

            // Avoid onload= expressions
            scriptPattern = pattern8;
            rlt = scriptPattern.matcher(rlt).replaceAll("");
        }
        return rlt;
    }

    /**
     * 过滤SQL注入内容
     */
    public static String stripSqlInjection(String value) {
        //value.replaceAll("('.+--)|(--)|(\\|)|(%7C)", "");
        return (null == value) ? null : value.replaceAll("('.+--)|(--)|(%7C)", "");
    }

    /**
     * 过滤SQL/XSS注入内容
     */
    public static String stripSqlXSS(String value) {
        return stripXSS(stripSqlInjection(value));
    }

}
