package com.circleon.domain.user.entity;

import com.circleon.common.BaseEntity;
import com.circleon.common.CommonStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "user_policy_agreement")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserPolicyAgreement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "service_terms", nullable = false)
    private boolean serviceTerms;

    @Column(name = "privacy_policies", nullable = false)
    private boolean privacyPolicies;

    @Column(name = "community_rules", nullable = false)
    private boolean communityRules;

    @Column(name = "status", nullable = false)
    private CommonStatus status;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    public static UserPolicyAgreement create(
            boolean serviceTerms, boolean privacyPolicies, boolean communityRules, Long userId) {
        UserPolicyAgreement userPolicyAgreement = new UserPolicyAgreement();

        userPolicyAgreement.serviceTerms = serviceTerms;
        userPolicyAgreement.privacyPolicies = privacyPolicies;
        userPolicyAgreement.communityRules = communityRules;
        userPolicyAgreement.userId = userId;
        userPolicyAgreement.status = CommonStatus.ACTIVE;

        return userPolicyAgreement;
    }

}
