package hotel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class hotelprova {

    static class Cliente {
        String nome;
        String documento;

        public Cliente(String nome, String documento) {
            this.nome = nome;
            this.documento = documento;
        }
    }

    static abstract class Quarto {
        int numero;
        int capacidade;
        double precoDiaria;

        public Quarto(int numero, int capacidade, double precoDiaria) {
            this.numero = numero;
            this.capacidade = capacidade;
            this.precoDiaria = precoDiaria;
        }

        public String getTipo() {
            return getClass().getSimpleName();
        }
    }

    static class QuartoSimples extends Quarto {
        public QuartoSimples(int numero) {
            super(numero, 2, 100.0);
        }
    }

    static class QuartoDuplo extends Quarto {
        public QuartoDuplo(int numero) {
            super(numero, 4, 180.0);
        }
    }

    static class Reserva {
        Cliente cliente;
        Quarto quarto;
        LocalDate dataEntrada;
        LocalDate dataSaida;
        boolean checkin = false;
        boolean checkout = false;

        public Reserva(Cliente cliente, Quarto quarto, LocalDate entrada, LocalDate saida) {
            this.cliente = cliente;
            this.quarto = quarto;
            this.dataEntrada = entrada;
            this.dataSaida = saida;
        }

        public double calcularTotal() {
            return quarto.precoDiaria * (dataSaida.toEpochDay() - dataEntrada.toEpochDay());
        }

        public boolean estaDisponivel(LocalDate data) {
            return data.isBefore(dataEntrada) || data.isAfter(dataSaida);
        }
    }

    static List<Cliente> clientes = new ArrayList<>();
    static List<Quarto> quartos = new ArrayList<>();
    static List<Reserva> reservas = new ArrayList<>();
    static Scanner sc = new Scanner(System.in);
    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static void main(String[] args) {
        int opcao;
        do {
            System.out.println("\n======= MENU HOTEL =======");
            System.out.println("1 - Cadastrar cliente");
            System.out.println("2 - Cadastrar quarto");
            System.out.println("3 - Reservar quarto");
            System.out.println("4 - Realizar check-in");
            System.out.println("5 - Realizar check-out");
            System.out.println("6 - Verificar disponibilidade");
            System.out.println("0 - Sair");
            System.out.print("Escolha uma opção: ");
            opcao = Integer.parseInt(sc.nextLine());

            switch (opcao) {
                case 1 -> cadastrarCliente();
                case 2 -> cadastrarQuarto();
                case 3 -> reservarQuarto();
                case 4 -> checkin();
                case 5 -> checkout();
                case 6 -> verificarDisponibilidade();
                case 0 -> System.out.println("Saindo...");
                default -> System.out.println("Opção inválida.");
            }
        } while (opcao != 0);
    }

    static void cadastrarCliente() {
        System.out.print("Nome do cliente: ");
        String nome = sc.nextLine();
        System.out.print("Documento do cliente: ");
        String doc = sc.nextLine();
        clientes.add(new Cliente(nome, doc));
        System.out.println("Cliente cadastrado com sucesso!");
    }

    static void cadastrarQuarto() {
        System.out.print("Número do quarto: ");
        int numero = Integer.parseInt(sc.nextLine());
        System.out.print("Tipo do quarto (1 - Simples / 2 - Duplo): ");
        int tipo = Integer.parseInt(sc.nextLine());

        if (tipo == 1) {
            quartos.add(new QuartoSimples(numero));
            System.out.println("Quarto simples cadastrado!");
        } else if (tipo == 2) {
            quartos.add(new QuartoDuplo(numero));
            System.out.println("Quarto duplo cadastrado!");
        } else {
            System.out.println("Tipo inválido!");
        }
    }

    static void reservarQuarto() {
        System.out.print("Documento do cliente: ");
        String doc = sc.nextLine();
        Cliente cliente = buscarCliente(doc);
        if (cliente == null) {
            System.out.println("Cliente não encontrado.");
            return;
        }

        System.out.print("Número do quarto: ");
        int numero = Integer.parseInt(sc.nextLine());
        Quarto quarto = buscarQuarto(numero);
        if (quarto == null) {
            System.out.println("Quarto não encontrado.");
            return;
        }

        System.out.print("Data de entrada (dd/MM/yyyy): ");
        LocalDate entrada = LocalDate.parse(sc.nextLine(), formatter);
        System.out.print("Data de saída (dd/MM/yyyy): ");
        LocalDate saida = LocalDate.parse(sc.nextLine(), formatter);

        if (!disponivel(quarto, entrada, saida)) {
            System.out.println("Quarto indisponível para o período.");
            return;
        }

        reservas.add(new Reserva(cliente, quarto, entrada, saida));
        System.out.println("Reserva realizada com sucesso!");
    }

    static void checkin() {
        System.out.print("Documento do cliente: ");
        String doc = sc.nextLine();
        for (Reserva r : reservas) {
            if (r.cliente.documento.equals(doc) && !r.checkin) {
                r.checkin = true;
                System.out.println("Check-in realizado.");
                return;
            }
        }
        System.out.println("Reserva não encontrada ou check-in já realizado.");
    }

    static void checkout() {
        System.out.print("Documento do cliente: ");
        String doc = sc.nextLine();
        for (Reserva r : reservas) {
            if (r.cliente.documento.equals(doc) && r.checkin && !r.checkout) {
                r.checkout = true;
                double total = r.calcularTotal();
                System.out.println("Check-out realizado. Total a pagar: R$ " + total);
                return;
            }
        }
        System.out.println("Reserva não encontrada ou check-out já realizado.");
    }

    static void verificarDisponibilidade() {
        System.out.print("Número do quarto: ");
        int numero = Integer.parseInt(sc.nextLine());
        Quarto quarto = buscarQuarto(numero);
        if (quarto == null) {
            System.out.println("Quarto não encontrado.");
            return;
        }

        System.out.print("Data desejada (dd/MM/yyyy): ");
        LocalDate data = LocalDate.parse(sc.nextLine(), formatter);

        boolean disponivel = true;
        for (Reserva r : reservas) {
            if (r.quarto.numero == numero && !data.isBefore(r.dataEntrada) && !data.isAfter(r.dataSaida)) {
                disponivel = false;
                break;
            }
        }
        System.out.println("Disponibilidade: " + (disponivel ? "DISPONÍVEL" : "INDISPONÍVEL"));
    }

    static Cliente buscarCliente(String doc) {
        for (Cliente c : clientes) {
            if (c.documento.equals(doc)) return c;
        }
        return null;
    }

    static Quarto buscarQuarto(int numero) {
        for (Quarto q : quartos) {
            if (q.numero == numero) return q;
        }
        return null;
    }

    static boolean disponivel(Quarto quarto, LocalDate entrada, LocalDate saida) {
        for (Reserva r : reservas) {
            if (r.quarto.numero == quarto.numero) {
                if (!(saida.isBefore(r.dataEntrada) || entrada.isAfter(r.dataSaida))) {
                    return false;
                }
            }
        }
        return true;
    }
}