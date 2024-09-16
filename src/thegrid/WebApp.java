/*


                  blank


*/

package thegrid;

import database.DBHandler;
import fi.iki.elonen.NanoHTTPD;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static common.Tools.extractResource;

public class WebApp extends NanoHTTPD {
    private final java.util.List<DBHandler.NameID> allFiles;
    private final UniqueRng ring;
    //int firstimg = -1;


    /**
     * Get indec from RowID
     * @param rowid
     * @return
     */
    int findIndex (int rowid) {
        for (int i=0; i<allFiles.size(); i++) {
            if (allFiles.get(i).rowid() == rowid)
                return i;
        }
        System.out.println("not found");
        return 0;
    }
    /**
     * Constructor,
     * start http server and initialisation
     */
    public WebApp() {
        super(80);
        allFiles = DBHandler.getInst().loadSelectedImageInfos(TheGrid.mainSQL.get().substring(0,42));
        ring = new UniqueRng(allFiles.size(), false);
        //ring.reset();

        try {
            start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
            System.out.println("\nRunning! Point your browsers to http://localhost \n");
        } catch (IOException e) {
            System.out.println("webserver start fail");
        }
    }

    /**
     * Send JPEG represented by byte[]
     *
     * @param bytes the JPEG
     * @return response object
     */
    private Response sendImageBytes(byte[] bytes) {
        InputStream is = new ByteArrayInputStream(bytes);
        try {
            return newFixedLengthResponse(Response.Status.OK,
                    "image/jpeg", is, is.available());
        } catch (Exception e) {
            return newFixedLengthResponse(Response.Status.BAD_REQUEST, "text/html", "Error");
        }
    }

    /**
     * Send whole image page
     *
     * @param session from serve function
     * @param rowid   the database id of the image
     * @return response object
     */
    private Response sendImagePage(IHTTPSession session, int rowid) {
        try {
            String str = new String(extractResource("imgpage0.html"));
            str = str.replace("@@THEIMG", rowid + ".jpg");
            return newFixedLengthResponse (str);
        } catch (Exception e) {
            return newFixedLengthResponse(Response.Status.BAD_REQUEST, "text/html", "Error");
        }
    }

    /**
     * Send the main page containing thumbnails
     *
     * @return response object
     */
    private Response sendDirectory() {
        StringBuilder msg = new StringBuilder("<html><body>\n");
        for (DBHandler.NameID nid : allFiles) {
            msg.append("<a href=\"")
                    .append(nid.rowid())
                    .append(".lnk\" target=\"_blank\"><img src=\"")
                    .append(nid.rowid())
                    .append(".tmb\" width=\"50\" height=\"50\"></a>\n");
        }
        msg.append("</body></html>\n");
        return newFixedLengthResponse(msg.toString());
    }

    /**
     * Send icon from the resource package
     *
     * @param uri requested icon
     * @return response object
     */
    private Response sendIcon(String uri) {
        byte[] bytes;
        try {
            bytes = extractResource(uri.substring(1));
            return sendImageBytes(bytes);
        } catch (Exception e) {
            return newFixedLengthResponse(Response.Status.BAD_REQUEST, "text/html", "Error");
        }
    }

    /**
     * send adjusted image page regarding to user action
     *
     * @param session request object from browser
     * @return response object
     */
    private Response switchImage(IHTTPSession session) {
        int rowid;
        String parm = session.getParms().get("img");
        if (parm.equals("@@PRV")) {
            rowid = allFiles.get(ring.getPrev()).rowid();
        } else if (parm.equals("@@NXT")) {
            rowid = allFiles.get(ring.getNext()).rowid();
        } else /* @@RES */ {
            rowid = 0;
        }
        return sendImagePage(null, rowid);
    }


    /**
     * main function of the server
     *
     * @param session input object from client
     * @return the response
     */
    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        int rowid = 0;
        String nude;
        try {
            nude = uri.substring(1, uri.length() - 4);
            rowid = Integer.parseInt(nude);
            //System.out.println("rowid: "+rowid);
        } catch (Exception e) {
            //throw new RuntimeException(e);
        }
        if (uri.equals("/")) {
            return sendDirectory();
        } else if (uri.endsWith(".tmb")) {
            DBHandler.ThumbHash tbh = DBHandler.getInst().loadThumbnail(rowid);
            return sendImageBytes(tbh.bt);
        } else if (uri.endsWith(".lnk")) {
            System.out.println("send linkpage: "+rowid);
            return sendImagePage(session, rowid);
        } else if (uri.endsWith(".jpg")) {
            byte[] bytes = DBHandler.getInst().loadImage(rowid);

            /////////////////
            System.out.println("send img2: "+rowid);
            int i = findIndex(rowid);
            ring.set(i);
            /////////////////

            return sendImageBytes(bytes);
        } else if (uri.endsWith(".ico")) {
            return sendIcon(uri);
        } else if (uri.startsWith("/show.html")) {
            return switchImage(session);
        }
        return null;
    }
}
