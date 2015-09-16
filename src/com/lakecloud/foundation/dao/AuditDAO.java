package com.lakecloud.foundation.dao;

import org.springframework.stereotype.Repository;

import com.lakecloud.core.base.GenericDAO;
import com.lakecloud.foundation.domain.Audit;


@Repository("auditDAO")
public class AuditDAO extends GenericDAO<Audit> {

}
