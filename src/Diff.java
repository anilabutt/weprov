import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Triple;
import org.apache.jena.graph.compose.Difference;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
//import org.apache.jena.sparql.core.DatasetGraphFactory.GraphMaker;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.util.iterator.ExtendedIterator;

import com.csiro.webservices.config.Configuration;

//import com.hp.hpl.jena.sparql.core.DatasetGraphMaker.GraphMaker;
//import org.apache.jena.graph.GraphMaker;

public class Diff {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Dataset baseDataSet= TDBFactory.createDataset("C:\\Users\\but21c\\Documents\\weprov");
		Model baseModel = baseDataSet.getDefaultModel(); 
		Model model = TDBFactory.createDataset(Configuration.getProperty(Configuration.STORE_PATH)).getDefaultModel();
		Graph graph1 = baseModel.getGraph();
		Graph graph2 = model.getGraph();
		
		Difference diff = new Difference ( graph2, graph1);
		ExtendedIterator<Triple> iter = diff.find();
		while (iter.hasNext()) {
			Triple tr = iter.next();
			System.out.println(tr.getSubject()  + "\t" + tr.getPredicate() + "\t" + tr.getObject());
		}
	}

}
