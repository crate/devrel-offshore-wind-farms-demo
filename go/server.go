package main

import (
	"context"
	"fmt"
	"log"
	"os"

	"github.com/gofiber/fiber/v2"
	"github.com/jackc/pgx/v5/pgxpool"
	"github.com/joho/godotenv"
)

func main() {
	err := godotenv.Load()
	if err != nil {
		log.Fatal("Error loading .env file!")
	}

	// Connect to CrateDB.
	dbpool, err := pgxpool.New(context.Background(), os.Getenv("CRATEDB_URL"))

	if err != nil {
		log.Fatalf("Error connecting to CrateDB: %v", err)
	}

	defer dbpool.Close()

	app := fiber.New()
	app.Static("/", "./public")

	app.Get("/api/windfarms", func(c *fiber.Ctx) error {
		conn, err := dbpool.Acquire(context.Background())

		if err != nil {
			log.Fatalf("Error acquiring connection: %v", err)
		}
		defer conn.Release()

		rows, err := conn.Query(context.Background(), "SELECT id, name, description, location, boundaries, turbines FROM windfarms ORDER BY id ASC")
		if err != nil {
			log.Fatalf("Error running query: %v", err)
		}
		defer rows.Close()

		return c.JSON(&fiber.Map{
			"results": nil,
		})
	})

	app.Get("/api/latest/:id", func(c *fiber.Ctx) error {
		// c.Params("id")
		return c.JSON(&fiber.Map{
			"results": nil,
		})
	})

	app.Get("/api/avgpctformonth/:id/:ts", func(c *fiber.Ctx) error {
		return c.JSON(&fiber.Map{
			"results": nil,
		})
	})

	app.Get("/api/outputforday/:id/:ts", func(c *fiber.Ctx) error {
		return c.JSON(&fiber.Map{
			"results": nil,
		})
	})

	app.Get("/api/dailymaxpct/:id/:days", func(c *fiber.Ctx) error {
		return c.JSON(&fiber.Map{
			"results": nil,
		})
	})

	app.Listen(fmt.Sprintf(":%s", os.Getenv("PORT")))
}
