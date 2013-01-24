import fr.dgac.ivy.Ivy;
import fr.dgac.ivy.IvyClient;
import fr.dgac.ivy.IvyException;
import fr.dgac.ivy.IvyMessageListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/** 
 * Listener of the Ivy Bus. In charge of answering the requests.
 *
 * @author Seb
 * @version 1.0
 */
public class IvyListener {

    /**
     * Bus used for the simulation.
     */
    private Ivy bus;

    /**
     * Map containing all the informations required to relay the request.
     */
    private Map map;

    /**
     * Visibility of the controler granted to the IvyListener.
     */
    private MapControler mapControler;

    private ArrayList<Integer> aircraftsList = new ArrayList<Integer>(5);
    private java.util.AbstractMap<Integer,String>  aircraftsFligthPlan = new HashMap<Integer, String>(5);

    private int DEFAULT_ID = 9999;

    /**
     * Constructor : connects the listener to the bus (creates it i needed)
     * and bind it to the regexp "sim (.*) WORLD_ENV_REQ (.*)".
     * 
     * @param m Map to be used
     * @param mc MapControler to be used
     * @throws IvyException
     */
    IvyListener (Map m, MapControler mc) throws IvyException {
        bus = new Ivy("NewGaia", "NewGaia ready", null);

        bus.bindMsg("^([0-9]+\\.[0-9]+ )?([^ ]*) +NEW_AIRCRAFT (.*|$)", new IvyMessageListener() {
            @Override
            public void receive(IvyClient ic, String[] arg) {
                if (arg.length>0) {
                    aircraftsList.add(Integer.valueOf(arg[2]));
                    mapControler.newUAV(Integer.valueOf(arg[2]), new SpaceCoordinates(0.0, 0.0, 0.0));
                    try {
                        bus.sendMsg("NewGaia " + DEFAULT_ID +" CONFIG_REQ " + arg[2]);
                    } catch (IvyException ex) {
                        System.out.println("Ivy failure : ");
                        System.out.println(ex.getMessage());
                    }
                }
            }
        });

        bus.bindMsg(DEFAULT_ID + " ground AIRCRAFTS (.*)", new IvyMessageListener() {

            @Override
            public void receive(IvyClient ic, String[] args) {
                if (args.length > 0) {
                    String[] aircrafts = args[0].split(",");
                    for (String id : aircrafts) {
                        aircraftsList.add(Integer.valueOf(id));
                        mapControler.newUAV(Integer.valueOf(id), new SpaceCoordinates(0.0, 0.0, 0.0));
                    }
                    if (!aircraftsList.isEmpty()) {
                        for (int id : aircraftsList) {
                            try {
                                bus.sendMsg("NewGaia " + DEFAULT_ID + " CONFIG_REQ " + id);
                            } catch (IvyException ex) {
                                System.out.println("Ivy failure : ");
                                System.out.println(ex.getMessage());
                            }
                        }
                    }

                }
            }
        });

        this.map = m;
        this.mapControler = mc;

        /**
         * Main aim : relaying the wind request
         */
        bus.bindMsg("sim (.*) WORLD_ENV_REQ (.*)", new IvyMessageListener () {
            @Override
            public void receive(IvyClient client, String[] args) {
                String nb_req = args[0];
                String buffer[] = new String[6];
                SpaceCoordinates coords = new SpaceCoordinates(0.0, 0.0, 0.0);
                if (args.length>1) {
                    buffer = args[1].split("\\u0020");
                    coords.setX(Double.parseDouble(buffer[3]));
                    coords.setY(Double.parseDouble(buffer[4]));
                    coords.setZ(Double.parseDouble(buffer[5]));
                    try {
                        SpaceCoordinates res;
                        try {
                            res = map.getWind(coords);
                        }
                        catch (NullPointerException e) {
                            res = new SpaceCoordinates(0.0, 0.0, 0.0);
                            System.out.print("error : ");
                            System.out.println(e.getStackTrace());
                        }
                        bus.sendMsg(nb_req + " gaia "
                                + "WORLD_ENV "
                                + res.getX()
                                + " "
                                + res.getY()
                                + " "
                                + res.getZ()
                                + " "
                                + "266.000000 "
                                + map.getTimeScale()
                                + " "
                                + "1"); //dispo GPS (=1)
                        int airCraftId = Integer.valueOf(client.getApplicationName().substring(14));
                        if(aircraftsList.contains(airCraftId)){
                            mapControler.setUAVCoordinates(Integer.valueOf(client.getApplicationName().substring(14)), coords);
                        }
                    } catch (IvyException ex) {
                        bus.stop();
                        System.out.println("Ivy failure : ");
                        System.out.println(ex.getMessage());
                    }
                }
            };
        });




        /*
         * Identifies the xml flight plan used and loads the precise 
         * coordinates of the point "HOME".
         */
        bus.bindMsg(DEFAULT_ID + " ground CONFIG ([1-9]+) ([^ ]+)", new IvyMessageListener() {

            @Override
            public void receive(IvyClient client, String[] args) {
                if (args.length>0) {
                    if (MainCoordinatesManager.getMainLandMark().getX()==0 && MainCoordinatesManager.getMainLandMark().getY()==0) {
                        initReferencePoint(args[1]);
                    }
                    if (!aircraftsFligthPlan.containsKey(Integer.valueOf(args[0]))) {
                        aircraftsFligthPlan.put(Integer.valueOf(args[0]), args[1]);
                    }
                    if (!aircraftsList.isEmpty()) {
                        mapControler.setHomePath(aircraftsFligthPlan.get(aircraftsList.get(0)));
                    }
                }
        }
                });

        bus.bindMsg("READY", new IvyMessageListener() {

            @Override
            public void receive(IvyClient client, String[] args) {
                if(client.getApplicationName().compareTo("Paparazzi server")==0) {
                    try {
                        bus.sendMsg("NewGaia 9999 AIRCRAFTS_REQ");
                    } catch (IvyException ex) {
                        System.out.println("Pb");
                    }
                }
            }
        });

        /**
         * Tries to connect to the default bus.
         */
        try {
            bus.start("127.255.255.255:2010");
        }
        catch (IvyException ie) {
            System.out.println("An issue occurred when trying to connect to the Ivy bus");
        }

        bus.sendMsg("NewGaia 9999 AIRCRAFTS_REQ");

    }

    /**
     * Uses the flight plan to load the main coordinates
     * @param s Path of the flight plan to use
     */
    public void initReferencePoint(String s) {
        try {
            Scanner scan = new Scanner(new File(s.substring(6)));
            scan.useDelimiter("lon0=");
            scan.next();
            scan.useDelimiter("\"");
            scan.next();
            String temp = scan.next();
            double x = java.lang.Double.valueOf(temp);
            scan.useDelimiter("lat0=");
            scan.next();
            scan.useDelimiter("\"");
            scan.next();
            temp = scan.next();
            double y = java.lang.Double.valueOf(temp);
            MainCoordinatesManager.setMainLandMark(x, y);
            scan.close();
        } catch (FileNotFoundException ex) {
            System.out.println("File not found");
        }
        catch (IOException ex) {
            System.out.println("Parsing error");
        }
    }

}
