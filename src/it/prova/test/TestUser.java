package it.prova.test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import it.prova.model.User;
import it.prova.service.MyServiceFactory;
import it.prova.service.user.UserService;

public class TestUser {

	public static void main(String[] args) {

		// parlo direttamente con il service
		UserService userService = MyServiceFactory.getUserServiceImpl();

		try {

			// ora con il service posso fare tutte le invocazioni che mi servono
			System.out.println("In tabella ci sono " + userService.listAll().size() + " elementi.");

			testInserimentoNuovoUser(userService);
			System.out.println("In tabella ci sono " + userService.listAll().size() + " elementi.");

			testRimozioneUser(userService);
			System.out.println("In tabella ci sono " + userService.listAll().size() + " elementi.");

			testFindByExample(userService);
			System.out.println("In tabella ci sono " + userService.listAll().size() + " elementi.");

			testUpdateUser(userService);
			System.out.println("In tabella ci sono " + userService.listAll().size() + " elementi.");

			// E TUTTI I TEST VANNO FATTI COSI'
			testCercaTuttiQuelliCheUsernameIniziaCon(userService);
			testCercaTuttiQuelliCreatiPrimaDi(userService);
			testCercaPerCognomeENomeCheInziaCon(userService);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void testInserimentoNuovoUser(UserService userService) throws Exception {
		System.out.println(".......testInserimentoNuovoUser inizio.............");
		User newUserInstance = new User("mauro", "rossi", "avavv", "bobobo", new Date());
		if (userService.inserisciNuovo(newUserInstance) != 1)
			throw new RuntimeException("testInserimentoNuovoUser FAILED ");

		System.out.println(".......testInserimentoNuovoUser PASSED.............");
	}

	private static void testRimozioneUser(UserService userService) throws Exception {
		System.out.println(".......testRimozioneUser inizio.............");
		// recupero tutti gli user
		List<User> interoContenutoTabella = userService.listAll();
		if (interoContenutoTabella.isEmpty() || interoContenutoTabella.get(0) == null)
			throw new Exception("Non ho nulla da rimuovere");

		Long idDelPrimo = interoContenutoTabella.get(0).getId();
		// ricarico per sicurezza con l'id individuato
		User toBeRemoved = userService.findById(idDelPrimo);
		if (userService.rimuovi(toBeRemoved) != 1)
			throw new RuntimeException("testRimozioneUser FAILED ");

		System.out.println(".......testRimozioneUser PASSED.............");
	}

	private static void testFindByExample(UserService userService) throws Exception {
		System.out.println(".......testFindByExample inizio.............");
		// inserisco i dati che poi mi aspetto di ritrovare
		userService.inserisciNuovo(new User("Asallo", "Bianchi", "pier", "pwd@1", new Date()));
		userService.inserisciNuovo(new User("Astolfo", "Verdi", "ast", "pwd@2", new Date()));

		// preparo un example che ha come nome 'as' e ricerco
		List<User> risultatifindByExample = userService.findByExample(new User("as"));
		if (risultatifindByExample.size() != 2)
			throw new RuntimeException("testFindByExample FAILED ");

		// se sono qui il test è ok quindi ripulisco i dati che ho inserito altrimenti
		// la prossima volta non sarebbero 2 ma 4, ecc.
		for (User userItem : risultatifindByExample) {
			userService.rimuovi(userItem);
		}

		System.out.println(".......testFindByExample PASSED.............");
	}

	private static void testUpdateUser(UserService userService) throws Exception {
		System.out.println(".......testUpdateUser inizio.............");

		// inserisco i dati che poi modifico
		if (userService.inserisciNuovo(new User("Giovanna", "Sastre", "gio", "pwd@3", new Date())) != 1)
			throw new RuntimeException("testUpdateUser: inserimento preliminare FAILED ");

		// recupero col findbyexample e mi aspetto di trovarla
		List<User> risultatifindByExample = userService.findByExample(new User("Giovanna", "Sastre"));
		if (risultatifindByExample.size() != 1)
			throw new RuntimeException("testUpdateUser: testFindByExample FAILED ");

		// mi metto da parte l'id su cui lavorare per il test
		Long idGiovanna = risultatifindByExample.get(0).getId();

		// ricarico per sicurezza con l'id individuato e gli modifico un campo
		String nuovoCognome = "Perastra";
		User toBeUpdated = userService.findById(idGiovanna);
		toBeUpdated.setCognome(nuovoCognome);
		if (userService.aggiorna(toBeUpdated) != 1)
			throw new RuntimeException("testUpdateUser FAILED ");

		System.out.println(".......testUpdateUser inizio.............");
	}

	public static void testCercaTuttiQuelliCheUsernameIniziaCon(UserService userService) throws Exception {
		System.out.println("...........testCercaTuttiQuelliCheUsernameIniziaCon   INIZIO.........");

		List<User> listaUserUsernameIniziaCon = userService.cercaTuttiQuelliCheUsernameIniziaCon("g");
		// if (listaUserUsernameIniziaCon.size() < 1)
		// throw new RuntimeException("test: testCercaTuttiQuelliCheUsernameIniziaCon
		// FAILED ");

		for (User userItem : listaUserUsernameIniziaCon) {
			System.out.println(userItem.getId() + " " + userItem.getNome() + " " + userItem.getCognome());
		}

		System.out.println("...........testCercaTuttiQuelliCheUsernameIniziaCon PASSED.........");
	}

	public static void testCercaTuttiQuelliCreatiPrimaDi(UserService userService) throws Exception {
		System.out.println("...........testCercaTuttiQuelliCreatiPrimaDi   INIZIO.........");

		Date dataCostruzioneDaConfrontare = new SimpleDateFormat("dd-MM-yyyy").parse("10-05-2022");
		List<User> listaUserCreatiPrimaDel = userService.cercaTuttiQuelliCreatiPrimaDi(dataCostruzioneDaConfrontare);
		if (listaUserCreatiPrimaDel.size() < 1)
			throw new RuntimeException("test: testCercaTuttiQuelliCreatiPrimaDi FAILED ");

		for (User userItem : listaUserCreatiPrimaDel) {
			System.out.println(userItem.getId() + " " + userItem.getNome() + " " + userItem.getCognome());
		}

		System.out.println("...........testCercaTuttiQuelliCreatiPrimaDi PASSED.........");
	}

	public static void testCercaPerCognomeENomeCheInziaCon(UserService userService) throws Exception {
		System.out.println("...........testCercaPerCognomeENomeCheInziaCon   INIZIO.........");

		List<User> listaUserConCognomeUgualeENomeCheIniziaCon = userService.cercaPerCognomeENomeCheInziaCon("rossi",
				"g");
		if (listaUserConCognomeUgualeENomeCheIniziaCon.size() < 1)
			throw new RuntimeException("test: testCercaPerCognomeENomeCheInziaCon FAILED ");

		for (User userItem : listaUserConCognomeUgualeENomeCheIniziaCon) {
			System.out.println(userItem.getId() + " " + userItem.getNome() + " " + userItem.getCognome());
		}

		System.out.println("...........testCercaPerCognomeENomeCheInziaCon PASSED.........");
	}

}
