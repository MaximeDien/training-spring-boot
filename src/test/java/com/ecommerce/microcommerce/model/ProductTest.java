package com.ecommerce.microcommerce.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class ProductTest {

    private Product produitA;
    private Product produitB;
    private Product produitC;
    private Product produitZ;
    private Product produitDefault;

    @Before
    public void initProduct() {
        produitA = new Product(1, new String("Ordinateur portable"), 350, 360);
        produitB = new Product(2, new String("Aspirateur Robot"), 500, 490);
        produitC = new Product(3, new String("Table de Ping Pong"), 750, 600);
        produitZ = new Product();
        produitDefault = new Product();
    }

    @Test
    public void getId() throws Exception {
        Assert.assertNotNull((produitA.getId()));
        Assert.assertNotNull((produitB.getId()));
        Assert.assertNotNull((produitC.getId()));
        Assert.assertEquals(produitA.getId(),1);
        Assert.assertEquals(produitB.getId(),2);
        Assert.assertEquals(produitC.getId(),3);
        Assert.assertTrue(produitA.getId()==1);
        Assert.assertTrue(produitB.getId()==2);
        Assert.assertTrue(produitC.getId()==3);
        Assert.assertFalse(produitA.getId()==3);
        Assert.assertFalse(produitB.getId()==1);
        Assert.assertFalse(produitC.getId()==2);
    }

    @Test
    public void setId() throws Exception {
        produitZ.setId(42);
        Assert.assertEquals(produitZ.getId(),42);
        Assert.assertTrue(produitZ.getId()==42);
    }

    @Test
    public void getNom() throws Exception {
        Assert.assertEquals(produitA.getNom(),"Ordinateur portable");
        produitA.setNom("Orange");
        Assert.assertTrue(produitA.getNom().equals("Orange"));
    }

    @Test
    public void setNom() throws Exception {
        produitZ.setNom("Mirai");
        Assert.assertEquals(produitZ.getNom(),"Mirai");
        Assert.assertNotEquals(produitZ.getNom(),"Miraizzzzzz");
    }

    @Test
    public void getPrix() throws Exception {
        produitB.setPrix(499);
        Assert.assertEquals(produitB.getPrix(),499);
        Assert.assertTrue(produitB.getPrix()==499);
        Assert.assertFalse(produitB.getPrix()==200);
    }

    @Test
    public void setPrix() throws Exception {
        produitZ.setPrix(999);
        Assert.assertTrue(produitZ.getPrix()==999);
    }

    @Test
    public void getPrixAchat() throws Exception {
        produitB.setPrixAchat(499);
        Assert.assertEquals(produitB.getPrixAchat(),499);
        Assert.assertTrue(produitB.getPrixAchat()==499);
        Assert.assertFalse(produitB.getPrixAchat()==200);
    }

    @Test
    public void setPrixAchat() throws Exception {
        produitZ.setPrixAchat(999);
        Assert.assertTrue(produitZ.getPrixAchat()==999);
    }

    @Test
    public void toStringProduct() throws Exception {
        Assert.assertEquals(produitDefault.toString(),"Product{id=0, nom='null', prix=0}");
    }

}