package ru.emelkrist.service.impl;

import org.springframework.stereotype.Service;
import ru.emelkrist.dao.AppUserDAO;
import ru.emelkrist.service.UserActivationService;
import ru.emelkrist.utils.CryptoTool;

@Service
public class UserActivationServiceImpl implements UserActivationService {
    private final AppUserDAO appUserDAO;
    private final CryptoTool cryptoTool;

    public UserActivationServiceImpl(AppUserDAO appUserDAO, CryptoTool cryptoTool) {
        this.appUserDAO = appUserDAO;
        this.cryptoTool = cryptoTool;
    }

    @Override
    public boolean activation(String cryptoUserId) {
        var id = cryptoTool.idOf(cryptoUserId);
        var optional = appUserDAO.findById(id);
        if (optional.isPresent()){
            var appUser = optional.get();
            appUser.setActive(true);
            appUserDAO.save(appUser);
            return true;
        }
        return false;
    }
}
