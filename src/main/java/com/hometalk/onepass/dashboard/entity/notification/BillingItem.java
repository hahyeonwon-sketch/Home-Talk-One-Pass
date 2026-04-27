package com.hometalk.onepass.dashboard.entity.notification;

import com.hometalk.onepass.dashboard.enums.ItemCategory;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public class BillingItem {

    @Enumerated(EnumType.STRING) // 여기서 사용!
    private ItemCategory category;
    private Integer item_amount;
}
