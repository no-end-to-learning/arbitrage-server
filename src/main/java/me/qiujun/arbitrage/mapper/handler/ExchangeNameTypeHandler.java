package me.qiujun.arbitrage.mapper.handler;

import com.google.common.base.Strings;
import me.qiujun.arbitrage.enums.ExchangeEnum;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ExchangeNameTypeHandler extends BaseTypeHandler<ExchangeEnum> {

    private final Map<String, ExchangeEnum> transformMap = new HashMap<>();

    {
        for (ExchangeEnum exchange : ExchangeEnum.values()) {
            transformMap.put(exchange.getName().toUpperCase(), exchange);
        }
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, ExchangeEnum parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter == null ? null : parameter.getName());
    }

    @Override
    public ExchangeEnum getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return this.fromExchangeName(rs.getString(columnName));
    }

    @Override
    public ExchangeEnum getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return this.fromExchangeName(rs.getString(columnIndex));
    }

    @Override
    public ExchangeEnum getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return this.fromExchangeName(cs.getString(columnIndex));
    }

    private ExchangeEnum fromExchangeName(String str) {
        if (Strings.isNullOrEmpty(str)) {
            return null;
        }

        return transformMap.get(str.toUpperCase());
    }
}


