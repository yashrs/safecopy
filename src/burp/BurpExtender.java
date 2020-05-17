package burp;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import javax.swing.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.copyOfRange;


public class BurpExtender implements IBurpExtender, IContextMenuFactory, ClipboardOwner {

    private final static String NAME = "Copy Request Safely(without creds/cookies)";

    private IBurpExtenderCallbacks callbacks;
    private final static String SESSION_VAR = "session";

    private IExtensionHelpers helpers;
    //
    // implement IBurpExtender
    //

    PrintWriter stdout;
    PrintWriter stderr;

    @Override
    public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {
        // keep a reference to our callbacks object
        this.callbacks = callbacks;

        // obtain an extension helpers object
        helpers = callbacks.getHelpers();

        // set our extension name
        callbacks.setExtensionName("Copy Request Safely Extension");

        // register ourselves as a message editor tab factory
        //callbacks.registerMessageEditorTabFactory(this);

        stdout = new PrintWriter(callbacks.getStdout(), true);
        stderr = new PrintWriter(callbacks.getStderr(), true);

        callbacks.registerContextMenuFactory(this);
        stdout.println("HELLO WORLD");

    }
    @Override
    public List<JMenuItem> createMenuItems(IContextMenuInvocation invocation) {
        final IHttpRequestResponse[] messages = invocation.getSelectedMessages();
        if (messages == null || messages.length == 0) return null;
        JMenuItem i1 = new JMenuItem(NAME);
        i1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copyMessages(messages, false);
            }
        });

        /*JMenuItem i2 = new JMenuItem(SESSION_MENU_ITEM);
        i2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copyMessages(messages, true);
            }
        });*/
        return Collections.singletonList(i1);
    }

    private void copyMessages(IHttpRequestResponse[] messages, boolean withSessionObject) {
        //StringBuilder py = new StringBuilder("import requests");
        //String requestsMethodPrefix =
        //        "\n" + (withSessionObject ? SESSION_VAR : "requests") + ".";

//        if (withSessionObject) {
//            //py.append("\n\n" + SESSION_VAR + " = requests.session()");
//        }
        stdout.println("HELLO WORLD");
        stdout.println(messages.length);

        for (IHttpRequestResponse message : messages)
        {
            IRequestInfo ri = helpers.analyzeRequest(message);

            List<String> headers = ri.getHeaders();

            byte[] req = message.getRequest();

            //String body = helpers.bytesToString();

            //stdout.println(message.getRequest());

            //message.getRequest();
            //stdout.println(helpers.bytesToString(req));

            int i;
            for (i=0; i<= headers.size() - 1; i++)
            {
                String header = headers.get(i);
                //stdout.println(header);
                //stdout.println("--");

                // skip any header that isn't an "X-Custom-Session-Id"
                if (header.toLowerCase().startsWith("authorization".toLowerCase())){
                    headers.set(i, "Authorization: REDACTED");
                    //stdout.println("*changed*");
                }
                if (header.toLowerCase().startsWith("cookie")){
                    headers.set(i, "Cookie: REDACTED");
                }
                if (header.toLowerCase().startsWith("x-csrf-token")){
                    headers.set(i, "X-Csrf-Token: REDACTED");
                }
                //stdout.print(headers.size());
            }
            stdout.println("-- After editing --");
            for (i=0; i<= headers.size() - 2; i++)
            {
                String header = headers.get(i);
                stdout.println(header);
            }
            stdout.println("-- Constructing request now --");

            byte[] temp  = helpers.buildHttpMessage(headers, Arrays.copyOfRange(req, ri.getBodyOffset(), req.length));
            stdout.println("-- DONE --");

            stdout.println("TEMP: ");
            stdout.println(helpers.bytesToString(temp));

            Toolkit.getDefaultToolkit().getSystemClipboard()
                .setContents(new StringSelection(helpers.bytesToString(temp)), this);


//            String prefix = "burp" + i++ + "_";
//            py.append("\n\n").append(prefix).append("url = \"");
//            py.append(escapeQuotes(ri.getUrl().toString()));
//            py.append('"');
//            List<String> headers = ri.getHeaders();
//            boolean cookiesExist = processCookies(prefix, py, headers);
//            py.append('\n').append(prefix).append("headers = {");
//            processHeaders(py, headers);
//            py.append('}');
//            BodyType bodyType = processBody(prefix, py, req, ri);
//            py.append(requestsMethodPrefix);
//            py.append(ri.getMethod().toLowerCase());
//            py.append('(').append(prefix).append("url, headers=");
//            py.append(prefix).append("headers");
//            if (cookiesExist) py.append(", cookies=").append(prefix).append("cookies");
//            if (bodyType != null) {
//                String kind = bodyType.toString().toLowerCase();
//                py.append(", ").append(kind).append('=').append(prefix).append(kind);
//            }
//            py.append(')');
        }

    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {

    }
}