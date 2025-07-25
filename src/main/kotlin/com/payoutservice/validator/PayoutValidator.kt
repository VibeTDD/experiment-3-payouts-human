import com.payoutservice.domain.Payout

interface PayoutValidator {

    fun validate(payout: Payout)
}