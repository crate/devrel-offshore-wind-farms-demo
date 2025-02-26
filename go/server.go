package main

import (
	"fmt"
	"log"
	"os"

	"github.com/gofiber/fiber/v2"
	"github.com/joho/godotenv"
)

func main() {
	err := godotenv.Load()
	if err != nil {
		log.Fatal("Error loading .env file!")
	}

	app := fiber.New()
	app.Static("/", "./public")

	app.Get("/api/windfarms", func(c *fiber.Ctx) error {
		return c.SendString("TODO")
	})

	app.Get("/api/latest/:id", func(c *fiber.Ctx) error {
		// c.Params("id")
		return c.SendString("TODO")
	})

	app.Get("/api/avgpctformonth/:id/:ts", func(c *fiber.Ctx) error {
		return c.SendString("TODO")
	})

	app.Get("/api/outputforday/:id/:ts", func(c *fiber.Ctx) error {
		return c.SendString("TODO")
	})

	app.Get("/api/dailymaxpct/:id/:days", func(c *fiber.Ctx) error {
		return c.SendString("TODO")
	})

	app.Listen(fmt.Sprintf(":%s", os.Getenv("PORT")))
}
