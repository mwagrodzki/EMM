package servlet;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import classifying.Classifier;
import classifying.Tree;
import utils.Utils;

@Controller
@RequestMapping("/index")
public class IndexController {

	private static final Logger LOG = Logger.getLogger(IndexController.class.getName());

	private Tree tree;
	private Classifier c;

	private void writeDataToModel(ModelMap model) {
		model.addAttribute("treeHtml", tree.htmlString());
		model.addAttribute("treeJson", Utils.convertObjectToJSON(tree));
		model.addAttribute("tree", tree);
		model.addAttribute("root", tree.getRoot());
		model.addAttribute("rootJson", Utils.convertObjectToJSON(tree.getRoot()));
		model.addAttribute("nodesList", Utils.convertObjectListToJSON(Tree.nodes(tree.getRoot())));
		model.addAttribute("trainingSet", c.getTrainingSet());
		//model.addAttribute("errorRates", tree.prune());
	}

	@RequestMapping("/index")
	public String home(ModelMap model, HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		if (c == null) {
			c = new Classifier();
			c.generateTrainingSet();
		}
                LOG.log(Level.INFO, "Outlook {0}", c.gainRatio(Classifier.OUTLOOK));
                LOG.log(Level.INFO, "Temperature {0}", c.gainRatio(Classifier.TEMPERATURE));
                LOG.log(Level.INFO, "Humidity {0}", c.gainRatio(Classifier.HUMIDITY));
		LOG.log(Level.INFO, "Windy {0}", c.gainRatio(Classifier.WINDY));
		if (tree == null) {
			tree = new Tree(c);
			LOG.info("new tree");
		}
		LOG.info(Utils.convertObjectToJSON(tree.getRoot()));
		tree.print();		
		//double[] errors = tree.prune();
		//LOG.info("Errors before pruning: "+errors[0]);
		//LOG.info("Errors after pruning:  "+errors[1]);
		writeDataToModel(model);
		return "index";
	}

	@RequestMapping(value = "/query", method = RequestMethod.POST)
	@ResponseBody
	public String query(@RequestBody String query) {
		String dataString = query.substring(10, query.length() - 2);
		String[] data = dataString.split(",");
		String outlook = data[0];
		String temperature = data[1];
                String humidity = data[2];
                String windy = data[3];
		return tree.queryModel(outlook, temperature, humidity, windy) + "";
	}

}
