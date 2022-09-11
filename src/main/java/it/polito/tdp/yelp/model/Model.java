package it.polito.tdp.yelp.model;

import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.yelp.db.YelpDao;

public class Model {
	
	YelpDao dao;
	private Graph<Review, DefaultWeightedEdge> grafo;
	
	public Model() {
		dao = new YelpDao();
	}

	public void creaGrafo(Business locale) {
		grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		
		// Aggiunta dei vertici 
		Graphs.addAllVertices(this.grafo, this.dao.getAllReviewsWithBusiness(locale));
		
		// Aggiunta degli archi
		for (Review r1 : this.grafo.vertexSet()) {
			for (Review r2 : this.grafo.vertexSet()) {
				if (!r1.equals(r2)) {
					if (r1.getDate().isAfter(r2.getDate())) {
						long peso = ChronoUnit.DAYS.between(r2.getDate(), r1.getDate());
						if (peso > 0)
							Graphs.addEdge(this.grafo, r2, r1, peso);
					}
				}
			}
		}
	}
	
	public List<Review> getRecensioniConNumeroArchiUscentiMassimo() {
		List<Review> result = new LinkedList<>();
		
		int massimo = 0;
		for (Review r : this.grafo.vertexSet()) {
			int numeroArchiUscenti = this.grafo.outDegreeOf(r);
			if (numeroArchiUscenti > massimo) {
				massimo = numeroArchiUscenti;
			}
		}
		
		for (Review r : this.grafo.vertexSet()) {
			int numeroArchiUscenti = this.grafo.outDegreeOf(r);
			if (numeroArchiUscenti == massimo) {
				result.add(r);
			}
		}
		
		return result;
	}
	
	public int getNumeroArchiUscenti(Review review) {
		return this.grafo.outDegreeOf(review);
	}
	
	public List<String> getAllCitta(){
		return this.dao.getAllCitta();
	}
	
	public List<Business> getAllBusinessWithCitta(String citta){
		return this.dao.getAllBusinessWithCitta(citta);
	}
	
	public int getNumVertici() {
		return this.grafo.vertexSet().size();
	}
	
	public int getNumArchi() {
		return this.grafo.edgeSet().size();
	}
	
}
