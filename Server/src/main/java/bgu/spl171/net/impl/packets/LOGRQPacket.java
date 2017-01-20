package bgu.spl171.net.impl.packets;

/**
 * Login Request Packet
 */
public class LOGRQPacket extends Packet {

    private String userName;

    /**
     * Constructor
     * @param userName The name of the user who wishes to log in
     */
    public LOGRQPacket(String userName) {
        opCode = LOGRQ;
        this.userName = userName;
    }

    /**
     * Getter for the username
     * @return the username
     */
    public String getUserName() {
        return userName;
    }

}
