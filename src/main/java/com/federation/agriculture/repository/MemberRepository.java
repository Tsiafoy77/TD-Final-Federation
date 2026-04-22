package com.federation.agriculture.repository;
import com.federation.agriculture.config.DatabaseConfig;
import com.federation.agriculture.model.Member;
import com.federation.agriculture.model.Gender;
import com.federation.agriculture.model.MemberOccupation;
import java.sql.*;

import java.util.*;
public class MemberRepository {
    private final DatabaseConfig dbConfig;
    public MemberRepository(DatabaseConfig dbConfig) {
        this.dbConfig = dbConfig;
    }
    public Member save(Member member) {
        String sql = &quot;INSERT INTO member (id, first_name, last_name, birth_date,
                gender, &quot; +
                &quot;address, profession, phone_number, email, occupation, &quot; +
                &quot;registration_fee_paid, membership_dues_paid,
                membership_date) &quot; +
                &quot;VALUES (?, ?, ?, ?, CAST(? AS gender), ?, ?, ?, ?, CAST(? AS
        member_occupation), ?, ?, ?)&quot;;
        if (member.getId() == null || member.getId().isEmpty()) {
            member.setId(UUID.randomUUID().toString());
        }
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, member.getId());
            pstmt.setString(2, member.getFirstName());
            pstmt.setString(3, member.getLastName());
            pstmt.setDate(4, java.sql.Date.valueOf(member.getBirthDate()));
            pstmt.setString(5, member.getGender().name());
            pstmt.setString(6, member.getAddress());
            pstmt.setString(7, member.getProfession());
            pstmt.setInt(8, member.getPhoneNumber());
            pstmt.setString(9, member.getEmail());
            pstmt.setString(10, member.getOccupation().name());
            pstmt.setBoolean(11, member.isRegistrationFeePaid());
            pstmt.setBoolean(12, member.isMembershipDuesPaid());
            if (member.getMembershipDate() != null) {
                pstmt.setDate(13,
                        java.sql.Date.valueOf(member.getMembershipDate()));

            } else {
                pstmt.setNull(13, java.sql.Types.DATE);
            }
            pstmt.executeUpdate();
            return member;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    public Member findById(String id) {
        String sql = &quot;SELECT id, first_name, last_name, birth_date, gender, &quot; +
                &quot;address, profession, phone_number, email, occupation, &quot; +
                &quot;registration_fee_paid, membership_dues_paid, membership_date
                &quot; +
                &quot;FROM member WHERE id = ?&quot;;
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Member member = new Member();
                    member.setId(rs.getString(&quot;id&quot;));
                    member.setFirstName(rs.getString(&quot;first_name&quot;));
                    member.setLastName(rs.getString(&quot;last_name&quot;));
                    java.sql.Date birthDateSql = rs.getDate(&quot;birth_date&quot;);
                    if (birthDateSql != null) {
                        member.setBirthDate(birthDateSql.toLocalDate());
                    }
                    member.setGender(Gender.valueOf(rs.getString(&quot;gender&quot;)));
                    member.setAddress(rs.getString(&quot;address&quot;));
                    member.setProfession(rs.getString(&quot;profession&quot;));
                    member.setPhoneNumber(rs.getInt(&quot;phone_number&quot;));
                    member.setEmail(rs.getString(&quot;email&quot;));
                    member.setOccupation(MemberOccupation.valueOf(rs.getString(&quot;occupation&quot;)));
                    member.setRegistrationFeePaid(rs.getBoolean(&quot;registration_fee_paid&quot;));

                    member.setMembershipDuesPaid(rs.getBoolean(&quot;membership_dues_paid&quot;));
                    java.sql.Date membershipDateSql =
                            rs.getDate(&quot;membership_date&quot;);
                    if (membershipDateSql != null) {
                        member.setMembershipDate(membershipDateSql.toLocalDate());
                    }
                    return member;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public List&lt;Member&gt; findAllByIds(List&lt;String&gt; ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList&lt;&gt;();
        }
        String sql = &quot;SELECT id, first_name, last_name, birth_date, gender, &quot; +
                &quot;address, profession, phone_number, email, occupation, &quot; +
                &quot;registration_fee_paid, membership_dues_paid, membership_date
                &quot; +
                &quot;FROM member WHERE id = ANY(?)&quot;;
        List&lt;Member&gt; members = new ArrayList&lt;&gt;();
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            Array array = conn.createArrayOf(&quot;VARCHAR&quot;, ids.toArray());
            pstmt.setArray(1, array);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Member member = new Member();
                    member.setId(rs.getString(&quot;id&quot;));
                    member.setFirstName(rs.getString(&quot;first_name&quot;));
                    member.setLastName(rs.getString(&quot;last_name&quot;));
                    java.sql.Date birthDateSql = rs.getDate(&quot;birth_date&quot;);
                    if (birthDateSql != null) {

                        member.setBirthDate(birthDateSql.toLocalDate());
                    }
                    member.setGender(Gender.valueOf(rs.getString(&quot;gender&quot;)));
                    member.setAddress(rs.getString(&quot;address&quot;));
                    member.setProfession(rs.getString(&quot;profession&quot;));
                    member.setPhoneNumber(rs.getInt(&quot;phone_number&quot;));
                    member.setEmail(rs.getString(&quot;email&quot;));
                    member.setOccupation(MemberOccupation.valueOf(rs.getString(&quot;occupation&quot;)));
                    member.setRegistrationFeePaid(rs.getBoolean(&quot;registration_fee_paid&quot;));
                    member.setMembershipDuesPaid(rs.getBoolean(&quot;membership_dues_paid&quot;));
                    java.sql.Date membershipDateSql =
                            rs.getDate(&quot;membership_date&quot;);
                    if (membershipDateSql != null) {
                        member.setMembershipDate(membershipDateSql.toLocalDate());
                    }
                    members.add(member);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return members;
    }
}