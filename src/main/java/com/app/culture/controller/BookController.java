package com.app.culture.controller;

import com.app.culture.model.Book;
import com.app.culture.model.UserBook;
import com.app.culture.service.BookService;
import com.app.culture.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class BookController {

    @Autowired
    private BookService bookService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/searchbook")
    public String searchBookPage(HttpSession session, Model model) {
        // Obtener el ID del usuario desde la sesión
        Long userId = (Long) session.getAttribute("userId");
        
        // Si el usuario está autenticado, cargamos sus libros marcados
        if (userId != null) {
            Map<Long, String> userBookStatuses = new HashMap<>();
            List<UserBook> userBooks = userService.getUserBooks(userId);
            
            for (UserBook userBook : userBooks) {
                userBookStatuses.put(userBook.getBook().getId(), userBook.getStatus());
            }
            
            model.addAttribute("userBookStatuses", userBookStatuses);
        }
        
        return "searchbook";
    }
    
    @PostMapping("/searchbook")
    public String searchBook(@RequestParam String query, HttpSession session, Model model) {
        List<Book> books = bookService.searchAndSaveBooks(query);
        
        // Obtener el ID del usuario desde la sesión
        Long userId = (Long) session.getAttribute("userId");
        
        // Si el usuario está autenticado, verificamos qué libros ya ha marcado
        if (userId != null) {
            Map<Long, String> userBookStatuses = new HashMap<>();
            List<UserBook> userBooks = userService.getUserBooks(userId);
            
            for (UserBook userBook : userBooks) {
                userBookStatuses.put(userBook.getBook().getId(), userBook.getStatus());
            }
            
            model.addAttribute("userBookStatuses", userBookStatuses);
        }
        
        model.addAttribute("books", books);
        model.addAttribute("query", query);
        return "searchbook";
    }
    
    @PostMapping("/markBook")
    public String markBook(@RequestParam Long bookId,
                          @RequestParam String status,
                          @RequestParam(required = false) String query,
                          HttpSession session,
                          Model model) {
        // Obtener el ID del usuario desde la sesión
        Long userId = (Long) session.getAttribute("userId");
        
        if (userId != null) {
            userService.markBookForUser(userId, bookId, status);
            
            // Si hay una consulta de búsqueda, redirigir de nuevo a los resultados
            if (query != null && !query.isEmpty()) {
                // Ejecutar la misma búsqueda de nuevo para mantener resultados
                List<Book> books = bookService.searchAndSaveBooks(query);
                
                // Obtener estados actualizados de los libros del usuario
                Map<Long, String> userBookStatuses = new HashMap<>();
                List<UserBook> userBooks = userService.getUserBooks(userId);
                
                for (UserBook userBook : userBooks) {
                    userBookStatuses.put(userBook.getBook().getId(), userBook.getStatus());
                }
                
                model.addAttribute("userBookStatuses", userBookStatuses);
                model.addAttribute("books", books);
                model.addAttribute("marked", true);
                model.addAttribute("query", query);
                return "searchbook";
            }
            
            return "redirect:/searchbook?marked=true";
        }
        
        return "redirect:/login";
    }
    
    @GetMapping("/mybooks")
    public String myBooks(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        
        if (userId != null) {
            List<UserBook> read = userService.getUserBooksByStatus(userId, "leido");
            List<UserBook> following = userService.getUserBooksByStatus(userId, "en_seguimiento");
            List<UserBook> pending = userService.getUserBooksByStatus(userId, "pendiente");
            
            model.addAttribute("read", read);
            model.addAttribute("following", following);
            model.addAttribute("pending", pending);
            
            return "mybooks";
        }
        
        return "redirect:/login";
    }
    @PostMapping("/unmarkBook")
    public String unmarkBook(@RequestParam Long bookId,
                        @RequestParam(required = false) String query,
                        @RequestParam(required = false) String returnTo,
                        HttpSession session,
                        Model model) {
        // Obtener el ID del usuario desde la sesión
        Long userId = (Long) session.getAttribute("userId");
        
        if (userId != null) {
            try {
                userService.unmarkBookForUser(userId, bookId);
                
                // Si hay un parámetro de retorno, lo usamos para la redirección
                if (returnTo != null && returnTo.equals("mybooks")) {
                    return "redirect:/mybooks?unmarked=true";
                }
                
                // Si hay una consulta de búsqueda, redirigir de nuevo a los resultados
                if (query != null && !query.isEmpty()) {
                    // Ejecutar la misma búsqueda de nuevo para mantener resultados
                    List<Book> books = bookService.searchAndSaveBooks(query);
                    
                    // Obtener estados actualizados de los libros del usuario
                    Map<Long, String> userBookStatuses = new HashMap<>();
                    List<UserBook> userBooks = userService.getUserBooks(userId);
                    
                    for (UserBook userBook : userBooks) {
                        userBookStatuses.put(userBook.getBook().getId(), userBook.getStatus());
                    }
                    
                    model.addAttribute("userBookStatuses", userBookStatuses);
                    model.addAttribute("books", books);
                    model.addAttribute("unmarked", true);
                    model.addAttribute("query", query);
                    return "searchbook";
                }
                
                return "redirect:/searchbook?unmarked=true";
            } catch (Exception e) {
                // Manejar error
                return "redirect:/searchbook?error=true";
            }
        }
        
        return "redirect:/login";
    }
    // Añadir este nuevo método al controlador existente
    @GetMapping("/bookdetails/{id}")
    public String bookDetails(@PathVariable Long id, HttpSession session, Model model) {
        try {
            // Obtener detalles del libro
            Optional<Book> bookOpt = bookService.getBookById(id);
            
            if (bookOpt.isPresent()) {
                Book book = bookOpt.get();
                model.addAttribute("book", book);
                
                // Verificar si el usuario ha marcado este libro
                Long userId = (Long) session.getAttribute("userId");
                if (userId != null) {
                    Optional<UserBook> userBookOpt = userService.getUserBookByUserIdAndBookId(userId, id);
                    userBookOpt.ifPresent(userBook -> {
                        model.addAttribute("userBook", userBook);
                        model.addAttribute("status", userBook.getStatus());
                    });
                }
                
                return "bookdetails";
            } else {
                System.err.println("Libro con ID " + id + " no encontrado");
                return "redirect:/searchbook?error=booknotfound";
            }
        } catch (Exception e) {
            System.err.println("Error al acceder a detalles del libro: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/searchbook?error=exception";
        }
    }
}