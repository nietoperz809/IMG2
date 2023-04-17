/*


                  blank


*/

package thegrid;

import database.DBHandler;
import fi.iki.elonen.NanoHTTPD;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static common.Tools.extractResource;

public class WebApp extends NanoHTTPD {
    private final java.util.List<DBHandler.NameID> allFiles;
    private final UniqueRng ring;

    public WebApp() {
        super(80);
        allFiles = DBHandler.getInst().getImageFileNames();
        ring = new UniqueRng (allFiles.size());

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
        } catch (Exception e) {
            return newFixedLengthResponse(Response.Status.BAD_REQUEST, "text/html", "Error");
        }
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        int rowid = 0;
        String nude = "Empty";
        try {
            nude = uri.substring(1, uri.length() - 4);
            rowid = Integer.parseInt(nude);
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
            try {
                String str = new String (extractResource("imgpage0.html"));
                str = str.replace ("@@THEIMG", ""+rowid+".pix");
//                str = str.replace ("@@PRV", ""+allFiles.get(ring.getPrev()).rowid);
//                str = str.replace ("@@NXT", ""+allFiles.get(ring.getNext()).rowid);
                return newFixedLengthResponse (str);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (uri.endsWith(".pix")) {
            byte[] bytes = DBHandler.getInst().loadImage(rowid);
            return sendImageBytes(bytes);
        } else if (uri.endsWith(".ico")) {
            byte[] bytes = new byte[0];
            try {
                bytes = extractResource(uri.substring(1));
                return sendImageBytes(bytes);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (uri.startsWith("/show.html")) {
            Map<String, String> m = session.getParms();
            String parm = m.get("img");
            if (parm.equals("@@PRV")) {
                rowid = allFiles.get(ring.getPrev()).rowid;
            } else {
                rowid = allFiles.get(ring.getNext()).rowid;
            }
            try {
                String str = new String (extractResource("imgpage0.html"));
                str = str.replace ("@@THEIMG", ""+rowid+".pix");
                return newFixedLengthResponse (str);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
        return null;
    }
}
