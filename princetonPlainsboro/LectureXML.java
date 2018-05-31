/*
 * LectureXML.java
 *
 * Created on 5 janvier 2006, 18:26
 *
 * Lecture d'un document XML et transformation en instances Java
 */

package princetonPlainsboro;

import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import princetonPlainsboro.Acte.Type;
import princetonPlainsboro.Personne.Sexe;

/**
 * Lecture d'un document XML et transformation en instances Java.
 *
 * @author promayon
 */
public class LectureXML {
    /// nom du document XML a analyser
    private String nomFichier;
    private final static String repBase = "src/donnees/";
    
    // 'nomFichier' est le nom d'un fichier XML se trouvant dans le repertoire 'repBase' a lire :
    public LectureXML(String nomFichier) {
        this.nomFichier = nomFichier;
    }
    
    public DossierMedical getDossier() {
        DossierMedical dossierCourant = null;
        Date date = null;
        Medecin medecinCourant = null;
        Patient patientCourant= null;
        Secretaire secretaireCourant = null;
        ArrayList<Acte> actes = new ArrayList<Acte>();
        String donneesCourantes = "";
        String nomCourant = "";
        String prenomCourant = "";
        Specialite specialiteCourante = Specialite.autre;
        Code codeCourant = null;
        int coefCourant = 0;
        Type type=Type.diagnostique;
        int numid=0;
        ArrayList<Integer> numtel = new ArrayList<Integer>();
        NumSecu numsecu = null;
        String adresse=null;
        Sexe sexe=null;
        
        // analyser le fichier par StAX
        try {
            // instanciation du parser
            InputStream in = new FileInputStream(repBase + nomFichier);
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader parser = factory.createXMLStreamReader(in);

            // lecture des evenements
            for (int event = parser.next(); event != XMLStreamConstants.END_DOCUMENT; event = parser.next()) {
                // traitement selon l'evenement
                switch (event) {
                    case XMLStreamConstants.START_ELEMENT:
                        if (parser.getLocalName().equals("dossiers")) {
                            dossierCourant = new DossierMedical();
                        }
                        break;
                    case XMLStreamConstants.END_ELEMENT:
                        if (parser.getLocalName().equals("acte")) {
                            actes.add(new Acte(codeCourant, nomCourant, patientCourant, medecinCourant, date, type, coefCourant));
                        }                        
                        if (parser.getLocalName().equals("code")) {
                            codeCourant = getCode(donneesCourantes);
                            if (codeCourant==null) 
                                throw new XMLStreamException("Impossible de trouver le code d'acte = "+donneesCourantes);
                        }                        
                        if (parser.getLocalName().equals("coef")) {
                            coefCourant = Integer.parseInt(donneesCourantes);
                        }
                        if (parser.getLocalName().equals("date")) {
                            int annee = Integer.parseInt(donneesCourantes.substring(0, donneesCourantes.indexOf('-')));
                            int mois = Integer.parseInt(donneesCourantes.substring(donneesCourantes.indexOf('-')+1, donneesCourantes.lastIndexOf('-')));
                            int jour = Integer.parseInt(donneesCourantes.substring(donneesCourantes.lastIndexOf('-')+1, donneesCourantes.length()));
                            
                            date = new Date(jour, mois, annee);
                        }
                        if (parser.getLocalName().equals("ficheDeSoins")) {
                            FicheDeSoins f = new FicheDeSoins(patientCourant, medecinCourant, date);
                            // ajout des actes
                            for (int i=0;i<actes.size();i++) {
                                Acte a = (Acte) actes.get(i);
                                f.ajouterActe(a);                                
                            }
                            // effacer tous les actes de la liste
                            actes.clear();
                            // ajouter la fiche de soin au dossiers
                            dossierCourant.ajouterFiche(f);
                        }
                        if (parser.getLocalName().equals("secretaire")) {
                            secretaireCourant = new Secretaire(nomCourant, prenomCourant, sexe, date, numid);
                        }
                        if (parser.getLocalName().equals("medecin")) {
                            medecinCourant = new Medecin(nomCourant, prenomCourant, sexe, date, numid, specialiteCourante, numtel);
                        }
                        if (parser.getLocalName().equals("nom")) {
                            nomCourant = donneesCourantes;
                        }
                        if (parser.getLocalName().equals("patient")) {
                            patientCourant = new Patient(nomCourant, prenomCourant, sexe, date, numid, numsecu, adresse);
                        }
                        if (parser.getLocalName().equals("prenom")) {
                            prenomCourant = donneesCourantes;
                        }

                        break;
                    case XMLStreamConstants.CHARACTERS:
                        donneesCourantes = parser.getText();
                        break;
                } // end switch
            } // end while
            parser.close();
        } catch (XMLStreamException ex) {
            System.out.println("Exception de type 'XMLStreamException' lors de la lecture du fichier : " + nomFichier);
            System.out.println("Details :");
            System.out.println(ex);
        } catch (IOException ex) {
            System.out.println("Exception de type 'IOException' lors de la lecture du fichier : " + nomFichier);
            System.out.println("Verifier le chemin.");
            System.out.println(ex.getMessage());
        }
       
        return dossierCourant;
    }
    
    private static Code getCode(String code) {
        if (code.equals("C"))
            return Code.C;
        if (code.equals("CS"))
            return Code.CS;
        if (code.equals("CSC"))
            return Code.CSC;
        if (code.equals("APC"))
            return Code.APC;
        if (code.equals("APV"))
            return Code.APV;
        if (code.equals("APY"))
            return Code.APY;
        if (code.equals("AVY"))
            return Code.AVY;
        if (code.equals("APU"))
            return Code.APU;
        if (code.equals("CCP"))
            return Code.CCP;
        if (code.equals("U03"))
            return Code.U03;
        if (code.equals("U45"))
            return Code.U45;
        if (code.equals("COE"))
            return Code.COE;
        if (code.equals("CNPSY"))
            return Code.CNPSY;
        if (code.equals("CDE"))
            return Code.CDE;
        if (code.equals("V"))
            return Code.V;
        if (code.equals("VL"))
            return Code.VL;
        if (code.equals("VS"))
            return Code.VS;
        if (code.equals("VNPSY"))
            return Code.VNPSY;
        if (code.equals("K"))
            return Code.K;
        if (code.equals("KMB"))
            return Code.KMB;
        if (code.equals("Z"))
            return Code.Z;
        if (code.equals("ORT"))
            return Code.ORT;
        if (code.equals("TO"))
            return Code.TO;
        if (code.equals("SP"))
            return Code.SP;
        if (code.equals("SF"))
            return Code.SF;
        if (code.equals("FI"))
            return Code.FI;
        if (code.equals("AMS"))
            return Code.AMS;
        if (code.equals("AMK"))
            return Code.AMK;
        if (code.equals("AMC"))
            return Code.AMC;
        if (code.equals("AMI"))
            return Code.AMI;
        if (code.equals("AIS"))
            return Code.AIS;
        if (code.equals("DI"))
            return Code.DI;
        if (code.equals("AMP"))
            return Code.AMP;
        if (code.equals("POD"))
            return Code.POD;
        if (code.equals("AMO"))
            return Code.AMO;
        if (code.equals("AMY"))
            return Code.AMY;
        if (code.equals("VAC"))
            return Code.VAC;
        if (code.equals("FP"))
            return Code.FP;
        if (code.equals("KC"))
            return Code.KC;
        if (code.equals("KE"))
            return Code.KE;
        if (code.equals("KFA"))
            return Code.KFA;
        if (code.equals("KFB"))
            return Code.KFB;
        if (code.equals("PRO"))
            return Code.PRO;
        return null;           
    }
}

