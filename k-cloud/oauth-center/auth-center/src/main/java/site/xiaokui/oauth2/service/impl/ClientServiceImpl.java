package site.xiaokui.oauth2.service.impl;

import org.springframework.stereotype.Service;
import site.xiaokui.base.service.BaseService;
import site.xiaokui.oauth2.entity.Client;
import site.xiaokui.oauth2.service.ClientService;

import java.util.List;
import java.util.UUID;

/**
 * @author hk
 */
@Service
public class ClientServiceImpl extends BaseService<Client> implements ClientService {

    @Override
    public Client createClient(String clientName) {
        Client client = new Client();
        client.setClientName(clientName);
        client.setClientId(UUID.randomUUID().toString().replace("-", ""));
        client.setClientSecret(UUID.randomUUID().toString().replace("-", ""));
        super.insertReturnKey(client);
        return client;
    }

    @Override
    public void updateClient(Client client) {
        super.updateById(client);
    }

    @Override
    public void deleteClient(Long id) {
        Client client = new Client();
        client.setId(id);
        super.deleteById(client);
    }

    @Override
    public Client findOne(Long id) {
        return super.getById(id);
    }

    @Override
    public List<Client> findAll() {
        return super.all();
    }

    @Override
    public Client findByClientId(String clientId) {
        Client client = new Client();
        client.setClientId(clientId);
        return super.matchOne(client);
    }

    @Override
    public Client findByClientSecret(String clientSecret) {
        Client client = new Client();
        client.setClientSecret(clientSecret);
        return super.matchOne(client);
    }

    @Override
    public boolean checkClientId(String clientId) {
        Client client = new Client();
        client.setClientId(clientId);
        return super.match(client) != null;
    }

    @Override
    public boolean checkClientRequest(String clientId, String clientSecret) {
        Client client = new Client();
        client.setClientId(clientId);
        client.setClientSecret(clientSecret);
        return super.matchOne(client) != null;
    }
}
