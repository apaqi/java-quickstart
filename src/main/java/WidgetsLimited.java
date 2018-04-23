import com.structurizr.Workspace;
import com.structurizr.api.StructurizrClient;
import com.structurizr.documentation.Format;
import com.structurizr.documentation.StructurizrDocumentationTemplate;
import com.structurizr.model.*;
import com.structurizr.view.*;
/**
 * @ClassName:
 * @Description:
 * @author: 慎独
 * @date: 2018/4/23
 * @version: v1.0
 * @since: JDK 1.7
 */
public class WidgetsLimited {
    private static final long WORKSPACE_ID = 38925;
    private static final String API_KEY = "5afa3d1d-b3cd-4f89-9762-a0c8cd47ef6e";
    private static final String API_SECRET = "2b112440-8f86-48a8-a343-0f6877ddb5b9";

    private static final String EXTERNAL_TAG = "External";
    private static final String INTERNAL_TAG = "Internal";

    public static void main(String[] args) throws Exception {
        Workspace workspace = new Workspace("Widgets Limited", "Sells widgets to customers online.");
        Model model = workspace.getModel();
        ViewSet views = workspace.getViews();
        Styles styles = views.getConfiguration().getStyles();

        model.setEnterprise(new Enterprise("Widgets Limited"));

        Person customer = model.addPerson(Location.External, "Customer", "A customer of Widgets Limited.");
        Person customerServiceUser = model.addPerson(Location.Internal, "Customer Service Agent", "Deals with customer enquiries.");
        SoftwareSystem ecommerceSystem = model.addSoftwareSystem(Location.Internal, "E-commerce System", "Allows customers to buy widgets online via the widgets.com website.");
        SoftwareSystem fulfilmentSystem = model.addSoftwareSystem(Location.Internal, "Fulfilment System", "Responsible for processing and shipping of customer orders.");
        SoftwareSystem taxamo = model.addSoftwareSystem(Location.External, "Taxamo", "Calculates local tax (for EU B2B customers) and acts as a front-end for Braintree Payments.");
        taxamo.setUrl("https://www.taxamo.com");
        SoftwareSystem braintreePayments = model.addSoftwareSystem(Location.External, "Braintree Payments", "Processes credit card payments on behalf of Widgets Limited.");
        braintreePayments.setUrl("https://www.braintreepayments.com");
        SoftwareSystem jerseyPost = model.addSoftwareSystem(Location.External, "Jersey Post", "Calculates worldwide shipping costs for packages.");

        model.getPeople().stream().filter(p -> p.getLocation() == Location.External).forEach(p -> p.addTags(EXTERNAL_TAG));
        model.getPeople().stream().filter(p -> p.getLocation() == Location.Internal).forEach(p -> p.addTags(INTERNAL_TAG));

        model.getSoftwareSystems().stream().filter(ss -> ss.getLocation() == Location.External).forEach(ss -> ss.addTags(EXTERNAL_TAG));
        model.getSoftwareSystems().stream().filter(ss -> ss.getLocation() == Location.Internal).forEach(ss -> ss.addTags(INTERNAL_TAG));
        //图之间关系处理
        customer.interactsWith(customerServiceUser, "Asks questions to", "Telephone");
        customerServiceUser.uses(ecommerceSystem, "Looks up order information using");
        customer.uses(ecommerceSystem, "Places orders for widgets using");
        ecommerceSystem.uses(fulfilmentSystem, "Sends order information to");
        fulfilmentSystem.uses(jerseyPost, "Gets shipping charges from");
        ecommerceSystem.uses(taxamo, "Delegates credit card processing to");
        taxamo.uses(braintreePayments, "Uses for credit card processing");
        //C4模型之全景图
        SystemLandscapeView systemLandscapeView = views.createSystemLandscapeView("SystemLandscape", "The system landscape for Widgets Limited.");
        systemLandscapeView.addAllElements();
        //给ecommerceSystem 创建系统上下文图
        SystemContextView ecommerceSystemContext = views.createSystemContextView(ecommerceSystem, "EcommerceSystemContext", "The system context diagram for the Widgets Limited e-commerce system.");
        ecommerceSystemContext.addNearestNeighbours(ecommerceSystem);
        ecommerceSystemContext.remove(customer.getEfferentRelationshipWith(customerServiceUser));
        //给fulfilmentSystem 创建系统上下文图
        SystemContextView fulfilmentSystemContext = views.createSystemContextView(fulfilmentSystem, "FulfilmentSystemContext", "The system context diagram for the Widgets Limited fulfilment system.");
        fulfilmentSystemContext.addNearestNeighbours(fulfilmentSystem);
        //
        DynamicView dynamicView = views.createDynamicView("CustomerSupportCall", "A high-level overview of the customer support call process.");
        dynamicView.add(customer, customerServiceUser);
        dynamicView.add(customerServiceUser, ecommerceSystem);

        // add some documentation
        StructurizrDocumentationTemplate template = new StructurizrDocumentationTemplate(workspace);
        template.addSection("System Landscape", 1, Format.Markdown, "Here is some information about the Widgets Limited system landscape... ![](embed:SystemLandscape)");
        template.addContextSection(ecommerceSystem, Format.Markdown, "This is the context section for the E-commerce System... ![](embed:EcommerceSystemContext)");
        template.addContextSection(fulfilmentSystem, Format.Markdown, "This is the context section for the Fulfilment System... ![](embed:FulfilmentSystemContext)");

        // add some styling
        styles.addElementStyle(Tags.SOFTWARE_SYSTEM).shape(Shape.RoundedBox);
        styles.addElementStyle(Tags.PERSON).shape(Shape.Person);

        styles.addElementStyle(Tags.ELEMENT).color("#ffffff");
        styles.addElementStyle(EXTERNAL_TAG).background("#EC5381").border(Border.Dashed);
        styles.addElementStyle(INTERNAL_TAG).background("#B60037");

        StructurizrClient structurizrClient = new StructurizrClient(API_KEY, API_SECRET);
        structurizrClient.putWorkspace(WORKSPACE_ID, workspace);
    }
}
