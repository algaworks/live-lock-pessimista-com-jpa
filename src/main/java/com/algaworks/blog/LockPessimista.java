package com.algaworks.blog;

import com.algaworks.blog.model.Artigo;

import javax.persistence.*;

public class LockPessimista {

    private static final Integer ID = 1;

    public static void main(String[] args) {
        EntityManagerFactory entityManagerFactory = Persistence
                .createEntityManagerFactory("Blog-PU");

//        entendendoAsOpcoes(entityManagerFactory);
//        javaEOWorkbench(entityManagerFactory);
        casoMaisPratico(entityManagerFactory);
    }

    public static void casoMaisPratico(EntityManagerFactory entityManagerFactory) {
        EntityManager entityManager1 = entityManagerFactory.createEntityManager();
        EntityManager entityManager2 = entityManagerFactory.createEntityManager();

        Runnable runnable1Joao = () -> {
            entityManager1.getTransaction().begin();
            log(1, "Imediatamente antes find.");
            Artigo artigo1 = entityManager1.find(
                    Artigo.class, ID, LockModeType.PESSIMISTIC_WRITE);
            log(1, "Imediatamente após find.");

            artigo1.setConteudo("Alteração do João (TH1)");

            log(1, "Esperando 3 segundos...");
            esperar(3000);
            log(1, "Espera dos 3 segs terminada.");

            log(1, "Imediatamente antes do commit.");
            entityManager1.getTransaction().commit();
            log(1, "Imediatamente após o commit.");
        };

        Runnable runnable2Maria = () -> {
            log(2, "Esperando 100 milis...");
            esperar(100);
            log(2, "Espera dos 100 milis terminada.");

            entityManager2.getTransaction().begin();
            log(2, "Imediatamente antes find.");
            Artigo artigo2 = entityManager2.find(
                    Artigo.class, ID, LockModeType.PESSIMISTIC_WRITE);
            log(2, "Imediatamente após find.");

            artigo2.setConteudo(artigo2.getConteudo() + " + Alteração da Maria (TH2)");

            log(2, "Imediatamente antes do commit.");
            entityManager2.getTransaction().commit();
            log(2, "Imediatamente após o commit.");
        };

        Thread thread1 = new Thread(runnable1Joao);
        Thread thread2 = new Thread(runnable2Maria);

        thread1.start();
        thread2.start();
    }

    public static void javaEOWorkbench(EntityManagerFactory entityManagerFactory) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        entityManager.getTransaction().begin();
        log(1, "Imediatamente antes find.");
        Artigo artigo1 = entityManager.find(
                Artigo.class, ID, LockModeType.PESSIMISTIC_WRITE);
        log(1, "Imediatamente após find.");

        artigo1.setConteudo("Alteração do João (TH1)");

        log(1, "Esperando 25 segundos...");
        esperar(25000);
        log(1, "Espera dos 25 segs terminada.");

        log(1, "Imediatamente antes do commit.");
        entityManager.getTransaction().commit();
        log(1, "Imediatamente após o commit.");
    }

    public static void entendendoAsOpcoes(EntityManagerFactory entityManagerFactory) {
        EntityManager entityManager1 = entityManagerFactory.createEntityManager();
        EntityManager entityManager2 = entityManagerFactory.createEntityManager();


        entityManager1.getTransaction().begin();
        Artigo artigo1 = entityManager1.find(
                Artigo.class, ID, LockModeType.PESSIMISTIC_WRITE);
        artigo1.setConteudo("Alteração do João");
        entityManager1.getTransaction().commit();


        entityManager2.getTransaction().begin();
        Artigo artigo2 = entityManager2.find(
                Artigo.class, ID, LockModeType.PESSIMISTIC_WRITE);
        artigo2.setConteudo(artigo2.getConteudo() + " + Alteração da Maria");
        entityManager2.getTransaction().commit();


//        entityManager1.getTransaction().commit();
    }

    private static void log(Integer thread, String msg) {
        System.out.println("[THREAD_" + thread + "] " + msg);
    }

    private static void esperar(long milesegundos) {
        try {
            Thread.sleep(milesegundos);
        } catch (Exception e) {}
    }
}
