package task2_3;

interface Payment {
    double pay(double amount);
    String getDescription();
}

class CreditCardPayment implements Payment {
    public double pay(double amount) {
        return amount; // Không có phí mặc định
    }
    public String getDescription() {
        return "Thanh toán bằng Thẻ tín dụng";
    }
}

class PayPalPayment implements Payment {
    public double pay(double amount) {
        return amount;
    }
    public String getDescription() {
        return "Thanh toán bằng PayPal";
    }
}

abstract class PaymentDecorator implements Payment {
    protected Payment paymentBase;

    public PaymentDecorator(Payment paymentBase) {
        this.paymentBase = paymentBase;
    }
    
    public double pay(double amount) {
        return paymentBase.pay(amount);
    }
    
    public String getDescription() {
        return paymentBase.getDescription();
    }
}

class ProcessingFeeDecorator extends PaymentDecorator {
    private double feeRate = 0.02; // 2% phí xử lý

    public ProcessingFeeDecorator(Payment paymentBase) {
        super(paymentBase);
    }

    @Override
    public double pay(double amount) {
        double baseAmount = super.pay(amount); // Có thể decorator bên trong thay đổi base
        // Phí xử lý tính trên số tiền thanh toán thực tế ban đầu (hoặc sau khi discount v.v)
        // Ta mặc định baseAmount là số tiền cần thanh toán
        return baseAmount + (baseAmount * feeRate);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " + Phí xử lý (2%)";
    }
}

class DiscountDecorator extends PaymentDecorator {
    private double discountAmount = 10;

    public DiscountDecorator(Payment paymentBase) {
        super(paymentBase);
    }

    @Override
    public double pay(double amount) {
        double discountedAmount = super.pay(amount) - discountAmount;
        return discountedAmount > 0 ? discountedAmount : 0; // Đảm bảo số tiền không âm
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " - Mã giảm giá ($10)";
    }
}

public class Task2_3 {
    public static void main(String[] args) {
        double amount = 100.0;
        
        // Cơ bản
        Payment payment1 = new CreditCardPayment();
        System.out.println(payment1.getDescription() + " | Cần thu: " + payment1.pay(amount));
        
        // Thêm phí xử lý
        Payment payment2 = new ProcessingFeeDecorator(new PayPalPayment());
        System.out.println(payment2.getDescription() + " | Cần thu: " + payment2.pay(amount));
        
        // Thêm phí xử lý TRƯỚC rồi áp dụng mã giảm giá SAU
        Payment payment3 = new DiscountDecorator(new ProcessingFeeDecorator(new CreditCardPayment()));
        System.out.println(payment3.getDescription() + " | Cần thu: " + payment3.pay(amount));
    }
}
