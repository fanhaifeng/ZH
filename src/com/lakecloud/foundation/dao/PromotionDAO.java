package com.lakecloud.foundation.dao;

import org.springframework.stereotype.Repository;

import com.lakecloud.core.base.GenericDAO;
import com.lakecloud.foundation.domain.Promotion;

@Repository("promotionDAO")
public class PromotionDAO extends GenericDAO<Promotion> {
}