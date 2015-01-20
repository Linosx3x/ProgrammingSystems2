/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package programmingsystems;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import restServer.RestServer;

/**
 *
 * @author Linosx3x
 */
public class ProgrammingSystems {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            RestServer server=new RestServer();
        } catch (IOException ex) {
            Logger.getLogger(ProgrammingSystems.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Could not start the server");
        }
    }
    
}
