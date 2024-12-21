@Controller
public class RFMController {
    @Autowired
    private RFMService rfmService;

    @GetMapping("/rfm-analysis")
    public String getRFMAnalysis(Model model) {
        List<RFMResult> rfmResults = rfmService.getRFMAnalysis();
        model.addAttribute("rfmResults", rfmResults);
        return "RFM分析";
    }
}
