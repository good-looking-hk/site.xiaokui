package site.xiaokui.oauth2.service;


import site.xiaokui.oauth2.entity.Client;

import java.util.List;

/**
 * 第三方客户端接口
 * @author hk
 */
public interface ClientService {

    Client createClient(String clientName);

    void updateClient(Client client);

    void deleteClient(Long clientId);

    Client findOne(Long clientId);

    List<Client> findAll();

    Client findByClientId(String clientId);

    Client findByClientSecret(String clientSecret);

    boolean checkClientId(String clientId);

    boolean checkClientRequest(String clientId, String clientSecret);
}
