package pacAcceso;

import java.text.ParseException;
import java.util.List;
import java.util.Scanner;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;


import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.hibernate.Transaction;


public class Main {
    public static void main(String[] args) throws ParseException, SecurityException, RollbackException, HeuristicMixedException, HeuristicRollbackException, SystemException {
        Scanner scanner = new Scanner(System.in);
        boolean salir = false;

        // Crear una nueva sesión de Hibernate
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session session = sessionFactory.openSession();

        while (!salir) {
            System.out.println("- - - - - - - - - - - - - - - - —  - - — - - -");
            System.out.println("\t\tBiblioteca");
            System.out.println("- - - - - - - - - - - - - - - - - - - - - - - -");
            System.out.println("1- Insertar Libro");
            System.out.println("2- Insertar Lector");
            System.out.println("3- Listado Libros");
            System.out.println("4- Listado Lectores");
            System.out.println("5- Préstamo de libros");
            System.out.println("6- Devolución de libros");
            System.out.println("7- Ver Libro por ID");
            System.out.println("8- Ver Lector por ID");
            System.out.println("9- Salir");
            System.out.println("- - - - - - - - - - - - - - - - - - - - - - - - -");

            int opcion;
            do {
                System.out.print("Seleccione una opción: ");
                while (!scanner.hasNextInt()) {
                    System.out.println("Por favor, ingrese un número válido.");
                    System.out.print("Seleccione una opción: ");
                    scanner.next();
                }
                opcion = scanner.nextInt();
            } while (opcion <= 0);

            switch (opcion) {
                case 1:
                    // Lógica para insertar un libro
                    System.out.println("Inserte título del libro: ");
                    scanner.nextLine();
                    System.out.println("Título: ");
                    String titulo = scanner.nextLine();
                    System.out.println("Autor: ");
                    String autor = scanner.nextLine();
                    System.out.println("Año publicación: ");
                    int anioPublicacion = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("¿Está disponible? (Si/No): ");
                    String disponibleStr = scanner.nextLine().toLowerCase();
                    boolean disponible = disponibleStr.equals("si");

                    // Iniciar una transacción
                    Transaction tx1 = session.beginTransaction();
                    // Crear nuevo libro
                    Libro nuevoLibro = new Libro(titulo, autor, anioPublicacion, disponible);
                    // Guardar nuevo libro
                    session.save(nuevoLibro);
                    // Commit de la transacción
                    tx1.commit();
                    System.out.println("Libro ingresado correctamente: ");
                    System.out.println(nuevoLibro);

                    break;

                case 2:
                    // Lógica para insertar un lector
                    System.out.println("Inserte datos lector");
                    scanner.nextLine();
                    System.out.println("Nombre: ");
                    String nombreLector = scanner.nextLine();
                    System.out.println("Apellidos: ");
                    String apellidosLector = scanner.nextLine();
                    System.out.println("Email: ");
                    String emailLector = scanner.nextLine();

                    // Iniciar una transacción
                    Transaction tx2 = (Transaction) session.beginTransaction();
                    // Crear nuevo lector
                    Lector nuevoLector = new Lector(nombreLector, apellidosLector, emailLector);
                    // Guardar nuevo lector
                    session.save(nuevoLector);
                    // Commit de la transacción
                    tx2.commit();
                    System.out.println("Lector ingresado correctamente: ");
                    System.out.println(nuevoLector);

                    break;

                case 3:
                    // Lógica para listar libros
                    System.out.println("Listado Libros");
                    System.out.println("--------------");

                    // Consultar bbdd
                    Query<Libro> queryLibros = session.createQuery("FROM Libro", Libro.class);
                    List<Libro> libros = queryLibros.getResultList();

                    // Mostrar libros en forma de tabla
                    System.out.println("ID\tTítulo\t\tAutor\t\tAño de Publicación\tDisponible");
                    System.out.println("--------------------------------------------------------------");
                    for (Libro libro : libros) {
                        System.out.printf("%5d\t%30s\t%30s\t%10d\t\t\t%10s%n", libro.getId(), libro.getTitulo(),
                                libro.getAutor(), libro.getAnioPublicacion(),
                                libro.isDisponible() ? "Sí" : "No");
                    }

                    // Opción para borrar o actualizar libros
                    System.out.println("Opciones: ");
                    System.out.println("1. Borrar libro");
                    System.out.println("2. Actualizar libro");
                    System.out.println("3. Volver al menú principal");
                    System.out.print("Selecciona una opción: ");
                    int opcionBorrarActualizar = scanner.nextInt();
                    scanner.nextLine(); // Consumir el salto de línea

                    switch (opcionBorrarActualizar) {
                        case 1:
                            // Lógica para borrar libro
                            System.out.print("ID del libro que desea borrar: ");
                            Long idBorrar = scanner.nextLong(); // Cambiar a nextLong()
                            scanner.nextLine(); // Consumir el salto de línea
                            Libro libroBorrar = session.get(Libro.class, idBorrar);
                            if (libroBorrar != null) {
                                Transaction transaction = session.beginTransaction();
                                session.delete(libroBorrar);
                                transaction.commit();
                                System.out.println("Libro borrado correctamente.");
                            } else {
                                System.out.println("El libro con el ID especificado no existe.");
                            }
                            break;
                        case 2:
                            // Lógica para actualizar un libro
                            System.out.println("Opción Actualizar libro seleccionado");
                            
                            // Solicitar el ID del libro a actualizar
                            System.out.print("ID del libro que desea actualizar: ");
                            Long idActualizar = scanner.nextLong();
                            scanner.nextLine(); // Consumir el salto de línea
                            
                            // Buscar el libro en la base de datos
                            Libro libroActualizar = session.get(Libro.class, idActualizar);
                            
                            if (libroActualizar != null) {
                                // Mostrar los detalles del libro actual antes de la actualización
                                System.out.println("Detalles del Libro antes de la actualización:");
                                System.out.println("ID: " + libroActualizar.getId());
                                System.out.println("Título: " + libroActualizar.getTitulo());
                                System.out.println("Autor: " + libroActualizar.getAutor());
                                System.out.println("Año de Publicación: " + libroActualizar.getAnioPublicacion());
                                System.out.println("Disponible: " + (libroActualizar.isDisponible() ? "Sí" : "No"));
                                
                             // Solicitar los nuevos datos del libro
                                System.out.println("Ingrese los nuevos datos del libro:");
                                System.out.print("Título: ");
                                String nuevoTitulo = scanner.nextLine();
                                System.out.print("Autor: ");
                                String nuevoAutor = scanner.nextLine();
                                System.out.print("Año de Publicación: ");
                                int nuevoAnioPublicacion = scanner.nextInt();
                                scanner.nextLine(); // Consumir el salto de línea
                                
                                // Aplicar los cambios al libro
                                libroActualizar.setTitulo(nuevoTitulo);
                                libroActualizar.setAutor(nuevoAutor);
                                libroActualizar.setAnioPublicacion(nuevoAnioPublicacion);
                                
                                // Iniciar una transacción para guardar los cambios
                                Transaction transaction = session.beginTransaction();
                                session.update(libroActualizar);
                                transaction.commit();
                                
                                // Mostrar mensaje de éxito
                                System.out.println("Libro actualizado correctamente.");
                            } else {
                                System.out.println("El libro con el ID especificado no existe.");
                            }
                            break;
                        case 3:
                            // Volver al menú principal
                            System.out.println("Volviendo al menú principal");
                            break;
                        default:
                            System.out.println("Opción no válida. Por favor, seleccione una opción válida.");
                            break;
                    }
                    break;

                case 4:
                    // Lógica para listar lectores
                    System.out.println("Listado de Lectores");
                    System.out.println("--------------------");

                    // Consultar la base de datos para obtener todos los lectores
                    List<Lector> lectores = session.createQuery("FROM Lector", Lector.class).getResultList();

                    // Mostrar lectores en forma de tabla
                    System.out.println("ID\tNombre\t\tApellido\tEmail");
                    System.out.println("-----------------------------------");
                    for (Lector lector : lectores) {
                        System.out.printf("%5d\t%-20s\t%-20s\t%s%n", lector.getId(), lector.getNombre(),
                                lector.getApellido(), lector.getEmail());
                    }
                    break;

                case 5:
                    // Lógica para realizar un préstamo de libros
                    System.out.println("Préstamo de libros");
                    System.out.println("------------------");

                    System.out.print("Ingrese el ID del libro: ");
                    Long idLibroPrestamo = scanner.nextLong();
                    System.out.print("Ingrese el ID del lector: ");
                    Long idLectorPrestamo = scanner.nextLong();

                    // Buscar el libro y el lector en la base de datos
                    Libro libroPrestamo = session.get(Libro.class, idLibroPrestamo);
                    Lector lectorPrestamo = session.get(Lector.class, idLectorPrestamo);

                    if (libroPrestamo != null && lectorPrestamo != null) {
                        // Verificar si el libro está disponible para préstamo
                        if (libroPrestamo.isDisponible()) {
                            // Actualizar el estado del libro y guardar el préstamo en la base de datos
                            libroPrestamo.setDisponible(false);
                            Transaction txPrestamo = session.beginTransaction();
                            session.update(libroPrestamo);
                            txPrestamo.commit();
                            System.out.println("Préstamo realizado correctamente.");
                        } else {
                            System.out.println("El libro no está disponible para préstamo.");
                        }
                    } else {
                        System.out.println("No se encontró el libro o el lector en la base de datos.");
                    }
                    break;

                case 6:
                    // Lógica para realizar la devolución de libros
                    System.out.println("Devolución de libros");
                    System.out.println("--------------------");

                    System.out.print("Ingrese el ID del libro a devolver: ");
                    Long idLibroDevolucion = scanner.nextLong();

                    // Buscar el libro en la base de datos
                    Libro libroDevolucion = session.get(Libro.class, idLibroDevolucion);

                    if (libroDevolucion != null) {
                        // Verificar si el libro está prestado
                        if (!libroDevolucion.isDisponible()) {
                            // Actualizar el estado del libro y guardar la devolución en la base de datos
                            libroDevolucion.setDisponible(true);
                            Transaction txDevolucion = session.beginTransaction();
                            session.update(libroDevolucion);
                            txDevolucion.commit();
                            System.out.println("Devolución realizada correctamente.");
                        } else {
                            System.out.println("El libro no está prestado.");
                        }
                    } else {
                        System.out.println("No se encontró el libro en la base de datos.");
                    }
                    break;

                case 7:
                    // Lógica para ver un libro por su ID
                    System.out.println("Ver Libro por ID");
                    System.out.println("-----------------");

                    System.out.print("Ingrese el ID del libro: ");
                    Long idLibro = scanner.nextLong();
                    scanner.nextLine(); // Consumir el salto de línea

                    // Buscar el libro por su ID en la base de datos
                    Libro libro = session.get(Libro.class, idLibro);

                    if (libro != null) {
                        // Si se encuentra el libro, imprimir sus detalles
                        System.out.println("Detalles del Libro:");
                        System.out.println("ID: " + libro.getId());
                        System.out.println("Título: " + libro.getTitulo());
                        System.out.println("Autor: " + libro.getAutor());
                        System.out.println("Año de Publicación: " + libro.getAnioPublicacion());
                        System.out.println("Disponible: " + (libro.isDisponible() ? "Sí" : "No"));
                    } else {
                        System.out.println("No se encontró ningún libro con el ID proporcionado.");
                    }
                    break;

                case 8:
                    // Lógica para ver un lector por su ID
                    System.out.println("Ver Lector por ID");
                    System.out.println("------------------");

                    System.out.print("Ingrese el ID del lector: ");
                    Long idLector = scanner.nextLong();
                    scanner.nextLine(); // Consumir el salto de línea

                    // Buscar al lector por su ID en la base de datos
                    Lector lector = session.get(Lector.class, idLector);

                    if (lector != null) {
                        // Si se encuentra el lector, imprimir sus detalles
                        System.out.println("Detalles del Lector:");
                        System.out.println("ID: " + lector.getId());
                        System.out.println("Nombre: " + lector.getNombre());
                        System.out.println("Apellido: " + lector.getApellido());
                        System.out.println("Email: " + lector.getEmail());
                    } else {
                        System.out.println("No se encontró ningún lector con el ID proporcionado.");
                    }
                    break;

                case 9:
                    // Salir del programa
                    System.out.println("Gracias por utilizar la Biblioteca. ¡Vuelva pronto!");
                    salir = true;
                    break;

                default:
                    System.out.println("Opción no válida. Por favor, seleccione una opción válida del menú.");
                    break;
            }
        }

        scanner.close();

        // Cerrar la sesión de Hibernate
        session.close();
        sessionFactory.close();
    }
}