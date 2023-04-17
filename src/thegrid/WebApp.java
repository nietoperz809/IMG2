/*


                  blank


*/

package thegrid;

import database.DBHandler;
import fi.iki.elonen.NanoHTTPD;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class WebApp extends NanoHTTPD {
    private final java.util.List<DBHandler.NameID> allFiles;

    public WebApp() {
        super(80);
        allFiles = DBHandler.getInst().getImageFileNames();

        try {
            start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
            System.out.println("\nRunning! Point your browsers to http://localhost \n");
        } catch (IOException e) {
            System.out.println("webserver start fail");
        }
    }

    private Response sendImageBytes(byte[] bytes) {
        InputStream is = new ByteArrayInputStream(bytes);
        try {
            return newFixedLengthResponse(Response.Status.OK,
                    "image/jpeg", is, is.available());
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        int rowid = 0;
        try {
            rowid = Integer.parseInt(uri.substring(1, uri.length() - 4));
        } catch (Exception e) {
            //throw new RuntimeException(e);
        }
        if (uri.equals("/")) {
            StringBuffer msg = new StringBuffer ("<html><body>\n");
            for (DBHandler.NameID nid : allFiles) {
                msg.append("<a href=\"")
                        .append(nid.rowid)
                        .append(".lnk\"><img src=\"")
                        .append(nid.rowid)
                        .append (".jpg\" width=\"50\" height=\"50\"></a>\n");
            }
            msg.append("</body></html>\n");
            return newFixedLengthResponse(msg.toString());
        } else if (uri.endsWith(".jpg")) {
            byte[] bytes = DBHandler.getInst().loadThumbnail(rowid);
            return sendImageBytes(bytes);
        } else if (uri.endsWith(".lnk")) {
            byte[] bytes = DBHandler.getInst().loadImage(rowid);
            return sendImageBytes(bytes);
        }
        return null;
    }
}
