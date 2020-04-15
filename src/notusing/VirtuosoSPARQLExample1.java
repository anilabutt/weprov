package notusing;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;

import virtuoso.jena.driver.*;

public class VirtuosoSPARQLExample1 {

        /**
         * Executes a SPARQL query against a virtuoso url and prints results.
         */
        public static void main(String[] args) {

                String url;
                if(args.length == 0)
                    url = "jdbc:virtuoso://localhost:1111";
                else
                    url = args[0];

/*                      STEP 1                  */
                VirtGraph set = new VirtGraph (url, "dba", "dba");

/*                      STEP 2                  */

/*                      STEP 3                  */
/*              Select all data in virtuoso     */
                String sparql = "SELECT * FROM <http://weprov.csiro.au/> WHERE { ?s ?p ?o } ";

/*                      STEP 4                  */
                VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(sparql, set);

                ResultSet results = vqe.execSelect();
                while (results.hasNext()) {
                        QuerySolution result = results.nextSolution();
                  //  RDFNode graph = result.get("graph");
                    RDFNode s = result.get("s");
                    RDFNode p = result.get("p");
                    RDFNode o = result.get("o");
                    System.out.println( " { " + s + " " + p + " " + o + " . }");
                }
        }
}
