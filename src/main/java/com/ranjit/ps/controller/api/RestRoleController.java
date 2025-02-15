package com.ranjit.ps.controller.api;

import com.ranjit.ps.model.Role;
import com.ranjit.ps.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RestRoleController {

    @Autowired
    private RoleService roleService;

    // Create a new role
    @PostMapping
    public Role createRole(@RequestBody Role role) {
        return roleService.saveRole(role);
    }

    // Get all roles
    @GetMapping
    public List<Role> getAllRoles() {
        return roleService.getAllRoles();
    }

    // Get a role by ID
    @GetMapping("/{roleId}")
    public Role getRoleById(@PathVariable Long roleId) {
        return roleService.getRoleById(roleId).orElseThrow(() -> new RuntimeException("Role not found with id " + roleId));
    }

    // Update a role
    @PutMapping("/{roleId}")
    public Role updateRole(@PathVariable Long roleId, @RequestBody Role roleDetails) {
        return roleService.updateRole(roleId, roleDetails);
    }

    // Delete a role
    @DeleteMapping("/{roleId}")
    public void deleteRole(@PathVariable Long roleId) {
        roleService.deleteRole(roleId);
    }

    // Assign a role to a user
    @PutMapping("/assign/{roleId}/user/{email}")
    public String assignRoleToUser(@PathVariable Long roleId, @PathVariable String email) {
         roleService.assignRoleToUser(email, roleId);
         return "Success Role : "+roleId+"Assigned To :"+email;
    }
}
