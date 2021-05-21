package com.example.demo.service;

import com.example.demo.entity.Client;
import com.example.demo.entity.Role;
import com.example.demo.repository.ClientRepository;
import com.example.demo.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class ClientService implements UserDetailsService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Client client = clientRepository.findByNickname(s);
        if (client != null) {
            return new User(client.getNickname(), client.getPassword(), client.getRole());
        }
        throw new UsernameNotFoundException("Client doesn't exist");
    }

    public Client getClientByNickname(String nickname) {
        return clientRepository.findByNickname(nickname);
    }

    public Client createClient(Client client){
        Client temp = clientRepository.findByNickname(client.getNickname());
        if (temp == null) {
            Role role = roleRepository.findByRole("ROLE_USER");
            if (role!=null) {
                ArrayList<Role> roles = new ArrayList<>();
                roles.add(role);
                client.setRole(roles);
                client.setPassword(bCryptPasswordEncoder.encode(client.getPassword()));
                return clientRepository.save(client);
            }
        }

        return null;
    }


}
