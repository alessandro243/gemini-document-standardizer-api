package dev.alex.standardizer.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class Db_Utils {
    @Autowired
    private JdbcTemplate selector;

    public List<Map<String, Object>> select(Db_Utils database, String report_id){
        String sql = "SELECT unidade_auditora, loja_origem, volume_total_pecas, valor_total_retido, atraso_medio_dias FROM inventario_consolidado where unidade_auditora like ?";
        System.out.println(report_id + "<<<<<<<<<<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        return selector.queryForList(sql, "%" + report_id + "%");
    }
}
