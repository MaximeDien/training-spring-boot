package com.ecommerce.microcommerce;

import com.ecommerce.microcommerce.dao.ProductDao;
import com.ecommerce.microcommerce.model.Product;
import com.ecommerce.microcommerce.web.controller.ProductController;
import com.google.gson.Gson;
import net.minidev.json.JSONArray;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MicrocommerceApplicationTests {

	@Autowired
	private ProductController controller;

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ProductDao productDao;

	private Gson gsonBuilder;
	private Product produitA, produitB;

	@Before
    public void initProduct(){
        this.produitA = new Product(1, "Nitendo Switch", 350, 315);
        this.produitB = new Product(2, "GameCube Controller", 30, 15);
    }

	@Test
	public void contextLoads() {
        Assert.assertNotNull(controller);
        this.gsonBuilder = new Gson();
        Assert.assertNotNull(gsonBuilder);
    }

    @Test
    public void listeProduitsAsJson() throws Exception {
        List<Product> listeProduits = new ArrayList<>();
        listeProduits.add(produitA);
        listeProduits.add(produitB);

        given(productDao.findAll()).willReturn(listeProduits);

        mockMvc.perform(get("/Produits")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(produitA.getId())))
                .andExpect(jsonPath("$[0].nom", is(produitA.getNom())))
                .andExpect(jsonPath("$[0].prix", is(produitA.getPrix())))
                .andExpect(jsonPath("$[0].prixAchat", is(produitA.getPrixAchat())));
    }

    @Test
    public void afficherUnProduitAsJson() throws Exception {
        int produitID = 1;
        produitA.setId(produitID);

        given(productDao.findById(produitID)).willReturn(produitA);

        mockMvc.perform(get("/Produits/{1}", produitID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nom", is(produitA.getNom())))
                .andExpect(jsonPath("$.prix", is(produitA.getPrix())))
                .andExpect(jsonPath("$.prixAchat", is(produitA.getPrixAchat())));
    }

    @Test
    public void listeProduitsTriesAsJson() throws Exception {
        List<Product> listeProduits = new ArrayList<>();
        listeProduits.add(produitB);
        listeProduits.add(produitA);
        given(productDao.findAllByOrderByNom()).willReturn(listeProduits);

        mockMvc.perform(get("/Produits/tries")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(produitB.getId())))
                .andExpect(jsonPath("$[0].nom", is(produitB.getNom())))
                .andExpect(jsonPath("$[0].prix", is(produitB.getPrix())))
                .andExpect(jsonPath("$[0].prixAchat", is(produitB.getPrixAchat())));
    }


    @Test
    public void listeProduitsMargeAsJson() throws Exception {
        List<Product> listeProduits = new ArrayList<>();
        listeProduits.add(produitB);
        listeProduits.add(produitA);
        given(productDao.findAll()).willReturn(listeProduits);

        mockMvc.perform(get("/Produits/marge")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['" + produitA.toString() + "']", is(produitA.getPrix() - produitA.getPrixAchat())))
                .andExpect(jsonPath("$['" + produitB.toString() + "']", is(produitB.getPrix() - produitB.getPrixAchat())));

    }

    @Test
    public void ajouterProduitAsJson() throws Exception {

        Product produitZ = new Product(1, "Filco Majestouch 2 Yellow TKL Mechanical Keyboard", 125, 125);
        String newProductLocation = String.format("http://localhost/Produits/%s", produitZ.getId());

        given(productDao.save(Mockito.any(Product.class)))
                .willReturn(produitZ);

        Gson gsonBuilder = new Gson();
        String newProductAsJson = gsonBuilder.toJson(produitZ);

        mockMvc.perform(
                put("/Produits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newProductAsJson))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", newProductLocation));
    }

    @Test
    public void ajouterProduitGratuitAsJson() throws Exception {

        Product produitZ = new Product(1, "Filco Majestouch 2 Yellow TKL Mechanical Keyboard", 0, 125);

        given(productDao.save(Mockito.any(Product.class)))
                .willReturn(produitZ);

        Gson gsonBuilder = new Gson();
        String newProductAsJson = gsonBuilder.toJson(produitZ);

        mockMvc.perform(
                put("/Produits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newProductAsJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateProduitAsJson() throws Exception {

        Product produitZ = new Product(1, "Filco Majestouch 2 Yellow TKL Mechanical Keyboard", 125, 125);

        given(productDao.save(Mockito.any(Product.class)))
                .willReturn(produitZ);

        Gson gsonBuilder = new Gson();
        String newProductAsJson = gsonBuilder.toJson(produitZ);

        mockMvc.perform(
                post("/Produits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newProductAsJson))
                .andExpect(status().isOk());
    }

    @Test
    public void updateProduitGratuitExceptionAsJson() throws Exception {

        Product produitZ = new Product(1, "Filco Majestouch 2 Yellow TKL Mechanical Keyboard", 0, 125);

        given(productDao.save(Mockito.any(Product.class)))
                .willReturn(produitZ);

        Gson gsonBuilder = new Gson();
        String newProductAsJson = gsonBuilder.toJson(produitZ);

        mockMvc.perform(
                post("/Produits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newProductAsJson))
                .andExpect(status().isConflict());
    }

    @Test
    public void deleteProduitAsJson() throws Exception {

        Product produitZ = new Product(1, "Filco Majestouch 2 Yellow TKL Mechanical Keyboard", 125, 125);
        List<Product> listeProduits = new ArrayList<>();
        listeProduits.add(produitZ);
        listeProduits.remove(0);
        productDao.save(produitZ);
        productDao.delete(1);

        given(productDao.findAll()).willReturn(listeProduits);

        mockMvc.perform(get("/Produits")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void testListeProduitMargeAsJson() throws Exception {

        List<Product> listeProduits = new ArrayList<>();
        listeProduits.add(produitA);
        listeProduits.add(produitB);

        given(productDao.findAll()).willReturn(listeProduits);

        JSONArray produitAExpectedMargin = new JSONArray();
        produitAExpectedMargin.add(produitA.getPrix() - produitA.getPrixAchat());

        JSONArray produitBExpectedMargin = new JSONArray();
        produitBExpectedMargin.add(produitB.getPrix() - produitB.getPrixAchat());

        mockMvc.perform(get("/Produits/marge")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].*", is(produitAExpectedMargin)))
                .andExpect(jsonPath("$[1].*", is(produitBExpectedMargin)));
    }

}

