package rockermq.cli;


import org.apache.commons.cli.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

/**
 * 参考链接： https://www.jianshu.com/p/c3ae61787a42
 *
 * @author HK
 * @date 2021-03-09 10:26
 */
public class ScpCmdTest {

    private static String HELP_STRING = null;

    private static Options options = new Options();

    public static void main(String[] args) {
        initCliArgs(new String[]{"-u", "hk",
                "--password", "hk",
                "-h", "localhost",
                "--dst_path", "127.0.0.1",
                "-p", "8099",
               "--src_path", "/xiaokui"
        });
    }

    private static void initCliArgs(String[] args) {
        CommandLineParser commandLineParser = new DefaultParser();
        // help
        options.addOption("help", "usage help");
        // host
        options.addOption(Option.builder("h").argName("ipv4 or ipv6").required().hasArg(true).longOpt("host").type(String.class).desc("the host of remote server").build());
        // port
        options.addOption(Option.builder("P").hasArg(true).longOpt("port").type(Short.TYPE).desc("the port of remote server").build());
        // user
        options.addOption(Option.builder("u").required().hasArg(true).longOpt("user").type(String.class).desc("the user of remote server").build());
        // password
        options.addOption(Option.builder("p").required().hasArg(true).longOpt("password").type(String.class).desc("the password of remote server").build());
        // srcPath
        options.addOption(Option.builder("s").required().hasArg(true).longOpt("src_path").type(String.class).desc("the srcPath of local").build());
        // dstPath
        options.addOption(Option.builder("d").required().hasArg(true).longOpt("dst_path").type(String.class).desc("the dstPath of remote").build());

        CommandLine commandLine = null;
        try {
            commandLine = commandLineParser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage() + "\n" + getHelpString());
            System.exit(0);
        }
        if (commandLine.hasOption("h")) {
            System.out.println("has h:" + commandLine.getOptionValue("h"));
        }
        if (commandLine.hasOption("host")) {
            System.out.println("has host:" + commandLine.getOptionValue("host"));
        }
        if (commandLine.hasOption("-h")) {
            System.out.println("has -h:" + commandLine.getOptionValue("-h"));
        }
        if (commandLine.hasOption("--host")) {
            System.out.println("has --host:" + commandLine.getOptionValue("--host"));
        }
        if (commandLine.hasOption("hh")) {
            System.out.println("has hh:" + commandLine.getOptionValue("hh"));
        } else {
            System.out.println("has not hh:" + commandLine.getOptionValue("hh"));
        }
        System.out.println("输入正确:" + commandLine);
    }

    private static String getHelpString() {
        if (HELP_STRING == null) {
            HelpFormatter helpFormatter = new HelpFormatter();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            PrintWriter printWriter = new PrintWriter(byteArrayOutputStream);
            helpFormatter.printHelp(printWriter, HelpFormatter.DEFAULT_WIDTH, "scp -help", null,
                    options, HelpFormatter.DEFAULT_LEFT_PAD, HelpFormatter.DEFAULT_DESC_PAD, null);
            printWriter.flush();
            HELP_STRING = new String(byteArrayOutputStream.toByteArray());
            printWriter.close();
        }
        return HELP_STRING;
    }
}
