@Service
public class RFMService {
    @Autowired
    private OrderRepository orderRepository;

    public List<RFMResult> getRFMAnalysis() {
        List<Object[]> results = orderRepository.calculateRFM();
        List<RFMResult> rfmResults = new ArrayList<>();

        for (Object[] row : results) {
            Integer customerId = (Integer) row[0];
            LocalDate lastOrderDate = (LocalDate) row[1];
            Long frequency = (Long) row[2];
            Double monetary = (Double) row[3];

            // 計算 Recency
            long recency = ChronoUnit.DAYS.between(lastOrderDate, LocalDate.now());

            rfmResults.add(new RFMResult(customerId, recency, frequency, monetary));
        }

        return rfmResults;
    }

    public String getSegment(long recency, long frequency, double monetary) {
        if (recency <= 30 && frequency > 5 && monetary > 1000) {
            return "VIP 顧客";
        } else if (recency <= 60 && frequency > 3) {
            return "忠實顧客";
        } else {
            return "普通顧客";
        }
    }
    
}
