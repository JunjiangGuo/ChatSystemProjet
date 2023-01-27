package Controllers;

import Models.DataBase;
import Models.User;
import Packet.Packet;
import Protocols.TCPReceiver;
import Protocols.UDPReceiver;
import Protocols.UDPSender;
import Views.General;

import java.io.IOException;

public class ControllerChat
{
    private static Controller app;
    private UDPReceiver serverUDP;
    private UDPReceiver udplisten;

    public ControllerChat(Controller app)
    {
        this.setApp(app);
        setUdplisten(getApp().getUDPReceiver());
    }


    public static Controller getApp()
    {
        return app;
    }

    private void setApp(Controller app)
    {
        ControllerChat.app = app;
    }

    private void setUdplisten(UDPReceiver serverUDP)
    {
        this.serverUDP = serverUDP;
    }

    /**
     * Methode pour la connexion d'un utilisateur
     *
     * @param newUser User
     * @return boolean
     */
    public void Connexion(User newUser)
    {
        app.setActu(newUser);

        TCPReceiver tcp = new TCPReceiver("SERVEUR", app);
        tcp.start();

        Packet packet = new Packet();
        packet.setMessage("Pseudo");
        packet.setUser(newUser);
        app.getUdpSender().broadcast(packet);
    }


    public void editNickname(String newPseudo)
    {
        String oldPseudo = app.getActu().getPseudo();
        app.getActu().setPseudo(newPseudo);
        DataBase.updateMessages(oldPseudo, newPseudo);
        General.pseudoModif();
        // Mettre ici la méthode pour envoyer le broadcast aux autres utilisateur pour signaler le changement de pseudo
        Packet packet = new Packet();
        packet.setMessage("ChangePseudo");
        packet.setUser(app.getActu());
        app.getUdpSender().broadcast(packet);
    }


    public void Deconnexion()
    {
        getApp().getUDPReceiver().closeSocket();
        TCPReceiver.setOuvert(false);

        Packet packet = new Packet();
        packet.setMessage("Deconnexion");
        packet.setUser(getApp().getActu());
        UDPSender.broadcast(packet);

        General.dispose();
        System.exit(0);
    }


}