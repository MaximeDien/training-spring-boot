package com.ecommerce.microcommerce.web.controller;

import com.ecommerce.microcommerce.dao.ProductDao;
import com.ecommerce.microcommerce.model.Product;
import com.ecommerce.microcommerce.web.exceptions.ProduitGratuitException;
import com.ecommerce.microcommerce.web.exceptions.ProduitIntrouvableException;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Api( description="API pour les opérations CRUD sur les produits.")

@RestController
public class ProductController {

    @Autowired
    private ProductDao productDao;


    //Récupérer la liste des produits

    @RequestMapping(value = "/Produits", method = RequestMethod.GET)
    @ApiOperation(value = "Récupére la liste des produits!")

    public MappingJacksonValue listeProduits() {

        Iterable<Product> produits = productDao.findAll();

        for (Product product : produits) {
            if (product.getPrix() <= 0) {
                throw new ProduitGratuitException("ProduitGratuitException ID = " + product.getId() +" : prix = 0 ");
            }
        }

        SimpleBeanPropertyFilter monFiltre = SimpleBeanPropertyFilter.serializeAllExcept("prixAchat");

        FilterProvider listDeNosFiltres = new SimpleFilterProvider().addFilter("monFiltreDynamique", monFiltre);

        MappingJacksonValue produitsFiltres = new MappingJacksonValue(produits);

        produitsFiltres.setFilters(listDeNosFiltres);

        return produitsFiltres;
    }


    //Récupérer un produit par son Id
    @ApiOperation(value = "Récupère un produit grâce à son ID à condition que celui-ci soit en stock!")
    @GetMapping(value = "/Produits/{id}")

    public Product afficherUnProduit(@PathVariable int id) {

        Product produit = productDao.findById(id);

        if(produit==null) throw new ProduitIntrouvableException("ProduitIntraouvableException " + id);

        if(produit.getPrix() <= 0){
            throw new ProduitGratuitException("ProduitGratuitException ID = " + id +" : prix = 0 ");
        }

        return produit;
    }




    //ajouter un produit
    //@PostMapping(value = "/Produits")


    @PutMapping(value = "/Produits")
    @ApiOperation(value = "Ajoute un produit")
    public ResponseEntity<Void> ajouterProduit(@Valid @RequestBody Product product) {

        Product productAdded =  productDao.save(product);

        if (productAdded == null)
            return ResponseEntity.noContent().build();

        if(productAdded.getPrix() <= 0){
            throw new ProduitGratuitException("ProduitGratuitException ID = " + productAdded.getId() +" : prix = 0 ");
        }

           URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(productAdded.getId())
            .toUri();

        return ResponseEntity.created(location).build();
}


    @DeleteMapping (value = "/Produits/{id}")
    @ApiOperation(value = "Supprime un produit")
    public void supprimerProduit(@PathVariable int id) {

        productDao.delete(id);
    }

    //@PutMapping (value = "/Produits")
    @PostMapping (value = "/Produits")
    @ApiOperation(value = "Sauvegarde les modifications sur un produit")
    public void updateProduit(@RequestBody Product product) {

        if(product.getPrix() <= 0){
            throw new ProduitGratuitException("ProduitGratuitException ID = " + product.getId() +" : prix = 0 ");
        }
        productDao.save(product);
    }


    //Pour les tests
    @GetMapping(value = "/test/produits/{prix}")
    @ApiOperation(value = "Test de recherche de produit par limite de prix")
    public List<Product>  testeDeRequetes(@PathVariable int prix) {

        return productDao.chercherUnProduitCher(400);
    }

    @GetMapping(value = "/Produits/tries")
    @ApiOperation(value = "Test de recherche de produit par limite de prix")
    public List<Product> getProductsListOrderedByName() {
        return productDao.findAllByOrderByNom();
    }

    @GetMapping(value = "/Produits/marge")
    @ApiOperation(value = "Calculer la marge de tous produits")
    public Map<Product, Double> calculerMargeProduit(){
        Iterable<Product> produits = productDao.findAll();

        Map<Product, Double> result = new HashMap<>();

        for (Product product:produits
                ) {
            result.put(product,(double)(product.getPrix() - product.getPrixAchat()));
        }

        return result;
    }



}
