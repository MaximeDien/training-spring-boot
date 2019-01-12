package com.ecommerce.microcommerce;

import com.ecommerce.microcommerce.dao.ProductDao;
import com.ecommerce.microcommerce.model.Product;
import com.ecommerce.microcommerce.web.controller.ProductController;
import com.google.gson.Gson;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        this.produitA = new Product(1, "Nitendo Switch", 350, 325);
        this.produitB = new Product(2, "GameCube Controller", 30, 47);
    }

	@Test
	public void contextLoads() {
        Assert.assertNotNull(controller);
        this.gsonBuilder = new Gson();
        Assert.assertNotNull(gsonBuilder);
    }

    @Test
    public void listeProduitsAsJson() throws Exception {
        List<Product> productsList = new ArrayList<>();
        productsList.add(produitA);
        productsList.add(produitB);

        given(productDao.findAll()).willReturn(productsList);

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
        List<Product> productsList = new ArrayList<>();
        productsList.add(produitB);
        productsList.add(produitA);
        given(productDao.findAllByOrderByNom()).willReturn(productsList);

        mockMvc.perform(get("/Produits/tries")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(produitB.getId())))
                .andExpect(jsonPath("$[0].nom", is(produitB.getNom())))
                .andExpect(jsonPath("$[0].prix", is(produitB.getPrix())))
                .andExpect(jsonPath("$[0].prixAchat", is(produitB.getPrixAchat())));
    }

}

